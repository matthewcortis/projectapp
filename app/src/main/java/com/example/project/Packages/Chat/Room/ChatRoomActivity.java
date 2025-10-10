package com.example.project.Packages.Chat.Room;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.Model.ChatMessageModel;
import com.example.project.Model.ChatRoomModel;
import com.example.project.Model.UserModel;
import com.example.project.Packages.Adapter.ChatAdapter;
import com.example.project.R;
import com.example.project.Utils.AndroidUtils;
import com.example.project.Utils.FirebaseUtils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatRoomActivity extends AppCompatActivity {
    String chatroomId;
    String currentUserID = FirebaseUtils.currentUserID();
    ChatRoomModel chatRoomModel;
    ChatAdapter adapter;
    private ImageButton backBtn;
    private CircleImageView imgAvatar;
    private ImageButton videoCallBtn, btnAttachment, voiceCallBtn, btnSend;
    Context context;
    private TextView tvName;
    private TextView tvStatus;
    private RecyclerView recyclerViewChat;
    private EditText messageInput;

    UserModel otherUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_room);
        viewImport();
        getOrCreateChatroomModel();
        listenEvents();
        setupChatRecyclerView();




    }
    private void listenEvents() {
        backBtn.setOnClickListener(v -> onBackPressed());
        btnAttachment.setOnClickListener(v -> openImagePicker());
        btnSend.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if(message.isEmpty())
                return;
            sendMessageToUser(message);
        });
    }


    private void setupChatRecyclerView() {
        Query query = FirebaseUtils.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options =
                new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                        .setQuery(query, ChatMessageModel.class)
                        .build();

        ChatAdapter adapter = new ChatAdapter(options, this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true); // hiển thị tin nhắn mới nhất ở đầu
        recyclerViewChat.setLayoutManager(manager);
        recyclerViewChat.setAdapter(adapter);
        adapter.startListening();

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerViewChat.smoothScrollToPosition(0);
            }
        });
    }

    private void sendMessageToUser(String message) {
        // Cập nhật thông tin chatroom
        chatRoomModel.setLastMessageTimestamp(Timestamp.now());
        chatRoomModel.setLastMessageSenderId(currentUserID);
        chatRoomModel.setLastMessage(message);
        chatRoomModel.setStatusRead("0");
        FirebaseUtils.getChatroomReference(chatroomId).set(chatRoomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(
                message,
                currentUserID,
                Timestamp.now(),
                "0",
                "0"
        );

        FirebaseUtils.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        messageInput.setText(""); // clear ô nhập
                        //sendNotification(message); // gửi thông báo FCM
                    }
                });
    }

    private void sendImageMessage(String imageUrl) {
        HashMap<String, Object> message = new HashMap<>();
        message.put("timestamp", Timestamp.now());
        message.put("message", imageUrl);
        message.put("senderId", currentUserID);
        message.put("images", "1");  // cờ báo là ảnh
        message.put("statusRead", "0");

        chatRoomModel.setLastMessage("###image###");
        chatRoomModel.setLastMessageSenderId(currentUserID);
        chatRoomModel.setLastMessageTimestamp(Timestamp.now());
        chatRoomModel.setStatusRead("0");
        FirebaseUtils.getChatroomReference(chatroomId).set(chatRoomModel);

        FirebaseUtils.getChatroomMessageReference(chatroomId)
                .add(message)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        sendNotification("Đã gửi một hình ảnh");
                    }
                });
    }




    private void getOrCreateChatroomModel(){
        FirebaseUtils.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                chatRoomModel = task.getResult().toObject(ChatRoomModel.class);
                if (chatRoomModel==null){
                    chatRoomModel = new ChatRoomModel(
                            chatroomId,
                            Arrays.asList(currentUserID,otherUser.getUserId()),
                            Timestamp.now(),
                            "","", ""


                    );
                    FirebaseUtils.getChatroomReference(chatroomId).set(chatRoomModel);

                }
            }
        });
    }
    private final ActivityResultLauncher<Intent> pickImage =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        uploadImageToFirebaseStorage(imageUri);
                    }
                }
            });


    // 🔹 Gọi khi nhấn nút chọn ảnh
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*"); // chỉ chọn ảnh
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pickImage.launch(intent);
    }


    // 🔹 Upload ảnh lên Firebase Storage
    private void uploadImageToFirebaseStorage(Uri imageUri) {
        UploadTask uploadTask = FirebaseUtils.putImageChat().putFile(imageUri);

        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                task.getResult().getStorage().getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            sendImageMessage(imageUrl);
                        })
                        .addOnFailureListener(e -> AndroidUtils.showToast(context, "Không thể lấy URL ảnh"));
            } else {
                AndroidUtils.showToast(context,"Tải ảnh thất bại");
            }
        });
    }
    void sendNotification(String message){

        FirebaseUtils.currentUserDetails().get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                UserModel currentUser = task.getResult().toObject(UserModel.class);
                try{
                    JSONObject jsonObject  = new JSONObject();

                    JSONObject notificationObj = new JSONObject();
                    notificationObj.put("title",currentUser.getUsername());
                    notificationObj.put("body",message);

                    JSONObject dataObj = new JSONObject();
                    dataObj.put("userId",currentUser.getUserId());

                    jsonObject.put("notification",notificationObj);
                    jsonObject.put("data",dataObj);
                    jsonObject.put("to",otherUser.getFcmToken());

                    callApi(jsonObject);


                }catch (Exception e){

                }

            }
        });

    }
    void callApi(JSONObject jsonObject){
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(),JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization","Bearer AAAASqJTAdQ:APA91bGSYqemCbqkUD2hSM7HFMNRdDl-HN0EDxcrXKaCjNQJx5CL5qKXCZRYCNbItzQTVbOHhJHbhqMK3w74jfXfkqYNtHhzGiSbYfB39wZ_CQVDCeSdX4O-sXQiU9WZADotgzKhjUMK")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });

    }

    private void viewImport() {
        otherUser = (UserModel) getIntent().getSerializableExtra("otherUser");
        backBtn = findViewById(R.id.btnBack);
        imgAvatar = findViewById(R.id.imgAvatar);
        videoCallBtn = findViewById(R.id.btnVideoCall);
        voiceCallBtn = findViewById(R.id.btnVoiceCall);
        tvName = findViewById(R.id.tvName);
        tvStatus = findViewById(R.id.tvStatus);
        btnAttachment = findViewById(R.id.btnAttachment);
        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        messageInput = findViewById(R.id.editMessage);
        btnSend = findViewById(R.id.btnSend);
        chatroomId = FirebaseUtils.getChatroomId(currentUserID,otherUser.getUserId());
        chatRoomModel = new ChatRoomModel();
        AndroidUtils.loadOtherProfilePicture(this,imgAvatar,otherUser.getUserId());
        tvName.setText(otherUser.getUsername());
        context = this;


    }

}