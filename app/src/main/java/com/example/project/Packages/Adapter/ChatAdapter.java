package com.example.project.Packages.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.Model.ChatMessageModel;
import com.example.project.R;
import com.example.project.Utils.AndroidUtils;
import com.example.project.Utils.FirebaseUtils;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ChatAdapter  extends FirestoreRecyclerAdapter<ChatMessageModel, ChatAdapter.ChatViewHolder> {

    private final Context context;
    public ChatAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatViewHolder holder, int position, @NonNull ChatMessageModel model) {

        // Ẩn tất cả các layout trước để tránh lỗi hiển thị trùng
        holder.leftChatLayout.setVisibility(View.GONE);
        holder.rightChatLayout.setVisibility(View.GONE);
        holder.leftImageLayout.setVisibility(View.GONE);
        holder.rightImageLayout.setVisibility(View.GONE);

        // Nếu là tin nhắn ảnh
        if ("1".equals(model.getImages()) && model.getMessage() != null) {
            Uri imageUri = Uri.parse(model.getMessage());
            if (FirebaseUtils.currentUserID().equals(model.getSenderId())) {
                holder.rightImageLayout.setVisibility(View.VISIBLE);
                holder.cardImageRight.setVisibility(View.VISIBLE);
                holder.leftImageLayout.setVisibility(View.GONE);
                holder.cardImageLeft.setVisibility(View.GONE);
                AndroidUtils.setImagePic(context, imageUri, holder.imageRight);
            } else {
                holder.leftImageLayout.setVisibility(View.VISIBLE);
                holder.cardImageLeft.setVisibility(View.VISIBLE);
                holder.rightImageLayout.setVisibility(View.GONE);
                holder.cardImageRight.setVisibility(View.GONE);
                AndroidUtils.setImagePic(context, imageUri, holder.imageLeft);
            }
        } else {
            // tin nhắn văn bản thông thường
            if (FirebaseUtils.currentUserID().equals(model.getSenderId())) {
                holder.rightChatLayout.setVisibility(View.VISIBLE);
                holder.leftChatLayout.setVisibility(View.GONE);
                holder.rightChatTextView.setText(model.getMessage());
            } else {
                holder.leftChatLayout.setVisibility(View.VISIBLE);
                holder.rightChatLayout.setVisibility(View.GONE);
                holder.leftChatTextView.setText(model.getMessage());
            }
        }
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_list_chat, parent, false);
        return new ChatViewHolder(view);
    }
    static class ChatViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftChatLayout, rightChatLayout, leftImageLayout, rightImageLayout;
        TextView leftChatTextView, rightChatTextView;
        ImageView imageLeft, imageRight;
        CardView cardImageLeft, cardImageRight;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextView = itemView.findViewById(R.id.left_chat_textview);
            rightChatTextView = itemView.findViewById(R.id.right_chat_textview);

            leftImageLayout = itemView.findViewById(R.id.left_image_layout);
            rightImageLayout = itemView.findViewById(R.id.right_image_layout);
            imageLeft = itemView.findViewById(R.id.image_left);
            imageRight = itemView.findViewById(R.id.image_right);
            cardImageLeft = itemView.findViewById(R.id.cardImageLeft);
            cardImageRight = itemView.findViewById(R.id.cardImageRight);
        }
    }
}
