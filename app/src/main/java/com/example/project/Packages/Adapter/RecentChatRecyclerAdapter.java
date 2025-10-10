package com.example.project.Packages.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.Model.ChatRoomModel;
import com.example.project.Model.UserModel;
import com.example.project.Packages.Chat.Room.ChatRoomActivity;
import com.example.project.R;
import com.example.project.Utils.AndroidUtils;
import com.example.project.Utils.FirebaseUtils;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.makeramen.roundedimageview.RoundedImageView;

public class RecentChatRecyclerAdapter  extends FirestoreRecyclerAdapter<ChatRoomModel, RecentChatRecyclerAdapter.ChatroomModelViewHolder> {

    Context context;
    TextView notification;
    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatRoomModel> options, Context context, TextView notification) {
        super(options);
        this.context = context;
        this.notification = notification;
    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ChatRoomModel model) {

        FirebaseUtils.getOtherUserFromChatroom(model.getUserIds()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {

                UserModel otherUserModel = task.getResult().toObject(UserModel.class);
                boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(FirebaseUtils.currentUserID());

                // Load avatar
                FirebaseUtils.getOtherProfilePicStorageRef(otherUserModel.getUserId())
                        .getDownloadUrl()
                        .addOnSuccessListener(uri -> AndroidUtils.setProfilePic(context, uri, holder.profilePic));

                holder.usernameText.setText(otherUserModel.getUsername());
                holder.lastMessageTime.setText(AndroidUtils.timestampToString(model.getLastMessageTimestamp()));

                // 🔹 Xử lý loại tin nhắn (chỉ giữ ảnh & text)
                String lastMessage = model.getLastMessage() != null ? model.getLastMessage() : "";

                if (lastMessageSentByMe) {
                    if (lastMessage.contains("###sendImage%&*!")) {
                        holder.lastMessageText.setText("Bạn: Đã gửi một hình ảnh");
                    } else {
                        holder.lastMessageText.setText("Bạn: " + lastMessage);
                    }
                } else {
                    if (lastMessage.contains("###sendImage%&*!")) {
                        holder.lastMessageText.setText("Đã gửi một hình ảnh");
                    } else {
                        holder.lastMessageText.setText(lastMessage);
                    }
                }

                // 🔹 Làm đậm nếu tin nhắn chưa đọc
                if ("0".equals(model.getStatusRead()) && !lastMessageSentByMe) {
                    holder.usernameText.setTypeface(null, Typeface.BOLD);
                    holder.usernameText.setTextColor(Color.BLACK);
                    holder.lastMessageText.setTextColor(Color.rgb(40, 167, 241));
                    holder.lastMessageTime.setTextColor(Color.rgb(40, 167, 241));
                } else {
                    holder.usernameText.setTypeface(null, Typeface.NORMAL);
                    holder.usernameText.setTextColor(Color.rgb(117, 117, 117));
                    holder.lastMessageText.setTextColor(Color.rgb(117, 117, 117));
                    holder.lastMessageTime.setTextColor(Color.rgb(117, 117, 117));
                }

                // 🔹 Trạng thái online
                FirebaseUtils.status(otherUserModel.getUserId()).addSnapshotListener((value, error) -> {
                    if (value != null) {
                        String statusStr = value.getString("status");
                        if ("1".equals(statusStr)) {
                            holder.statusOnline.setVisibility(View.VISIBLE);
                        } else {
                            holder.statusOnline.setVisibility(View.GONE);
                        }
                    }
                });

                // 🔹 Click để mở ChatActivity
                holder.itemView.setOnClickListener(view -> {
                    if (!lastMessageSentByMe) {
                        FirebaseUtils.updateLastRead(model.getChatroomId()).update("statusRead", "1");
                    }
                    Intent intent = new Intent(context, ChatRoomActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("otherUser", otherUserModel);

                    context.startActivity(intent);
                });
            }
        });
    }


    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_message,parent,false);

        return new ChatroomModelViewHolder(view);
    }
    @Override
    public void onDataChanged() {
        super.onDataChanged();
        if (getItemCount() == 0) {
            notification.setVisibility(View.VISIBLE);
        } else {
            notification.setVisibility(View.GONE);
        }
    }
    static class ChatroomModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        TextView lastMessageText;
        TextView lastMessageTime;
        ImageView profilePic;
        RoundedImageView statusOnline;

        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.textViewName);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
            statusOnline = itemView.findViewById(R.id.onlineStatus);


        }
    }
}
