package com.example.project.Packages.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project.Model.NewFeedModel;
import com.example.project.Model.PreferenceManager;
import com.example.project.R;
import com.example.project.Utils.AndroidUtils;
import com.example.project.Utils.FirebaseUtils;

import java.util.ArrayList;
import java.util.List;

public class NewFeedRecyclerAdapter extends RecyclerView.Adapter<NewFeedRecyclerAdapter.NewFeedViewHolder> {

    private final Context context;
    private List<NewFeedModel> postList = new ArrayList<>();
    private PreferenceManager preferenceManager;

    public NewFeedRecyclerAdapter(Context context) {
        this.context = context;
    }

    public void setPosts(List<NewFeedModel> postList) {
        this.postList = postList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NewFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post_card, parent, false);
        return new NewFeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewFeedViewHolder holder, int position) {
        NewFeedModel model = postList.get(position);
        preferenceManager = new PreferenceManager(context.getApplicationContext());

        loadUserInfo(holder, model.getIdAuthor());
        holder.tvCaption.setText(model.getPostText());
        holder.tvLocation.setText(model.getLocation());
        holder.tvRating.setText(String.valueOf(model.getRating()));
        holder.tvTime.setText(AndroidUtils.getTimeAgo(model.getPostTimestamp()));
        holder.tvLikes.setText(model.getLikeCount() + " likes");

        // --- Hiển thị ảnh hoặc video ---
        if (model.getPostMedia() != null && !model.getPostMedia().isEmpty()) {
            if ("1".equals(model.getImageStatus())) {
                holder.imageBackgroundCardView.setVisibility(View.VISIBLE);
                holder.videoBackgroundCardView.setVisibility(View.GONE);

                Uri imageUri = Uri.parse(model.getPostMedia());
                AndroidUtils.setImagePic(context, imageUri, holder.imgPost);

            } else if ("1".equals(model.getVideoStatus())) {
                holder.videoBackgroundCardView.setVisibility(View.VISIBLE);
                holder.imageBackgroundCardView.setVisibility(View.GONE);

                Uri videoUri = Uri.parse(model.getPostMedia());
                holder.videoStoryPost.setVideoURI(videoUri);
                MediaController mediaController = new MediaController(context);
                mediaController.setAnchorView(holder.videoStoryPost);
                holder.videoStoryPost.setMediaController(mediaController);
                holder.videoStoryPost.setOnPreparedListener(mp -> {
                    mp.setLooping(true);
                    mediaController.show(0);
                });

            } else {
                holder.imageBackgroundCardView.setVisibility(View.GONE);
                holder.videoBackgroundCardView.setVisibility(View.GONE);
                holder.videoStoryPost.stopPlayback();
            }
        } else {
            holder.imageBackgroundCardView.setVisibility(View.GONE);
            holder.videoBackgroundCardView.setVisibility(View.GONE);
            holder.videoStoryPost.stopPlayback();
        }
    }

    @Override
    public int getItemCount() {
        return postList != null ? postList.size() : 0;
    }

    // --- ViewHolder ---
    public static class NewFeedViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvLocation, tvRating, tvCaption, tvTime, tvLikes;
        ImageView imgAvatar, imgPost;
        VideoView videoStoryPost;
        CardView imageBackgroundCardView, videoBackgroundCardView;
        LinearLayout mediaLayout;

        public NewFeedViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvCaption = itemView.findViewById(R.id.tvCaption);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            imgPost = itemView.findViewById(R.id.imageStoryPost);
            tvTime = itemView.findViewById(R.id.tvTime);
            mediaLayout = itemView.findViewById(R.id.mediaLayoutStory);
            videoStoryPost = itemView.findViewById(R.id.videoStoryPost);
            imageBackgroundCardView = itemView.findViewById(R.id.imageStory);
            videoBackgroundCardView = itemView.findViewById(R.id.videoStory);
            tvLikes = itemView.findViewById(R.id.tvLikes);
        }
    }

    // --- Load thông tin người đăng ---
    private void loadUserInfo(NewFeedViewHolder holder, String userId) {
        AndroidUtils.loadOtherProfilePicture(context, holder.imgAvatar, userId);
        FirebaseUtils.otherUserDetails(userId)
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("username");
                        String avatarUrl = documentSnapshot.getString("avatarUrl");

                        holder.tvUsername.setText(username != null ? username : "Người dùng");
                        Glide.with(holder.itemView.getContext())
                                .load(avatarUrl)
                                .placeholder(R.drawable.baseline_user_24)
                                .circleCrop()
                                .into(holder.imgAvatar);
                    } else {
                        holder.tvUsername.setText("Người dùng");
                    }
                })
                .addOnFailureListener(e -> holder.tvUsername.setText("Lỗi tải user"));
    }
}
