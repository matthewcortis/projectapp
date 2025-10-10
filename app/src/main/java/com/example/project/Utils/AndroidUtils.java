package com.example.project.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.project.Model.UserModel;
import com.example.project.R;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AndroidUtils {
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void passUserModelAsIntent(Intent intent, UserModel model) {
        intent.putExtra(Constant.KEY_USER_ID, model.getUserId());
        intent.putExtra(Constant.KEY_USER_NAME, model.getUsername());
        intent.putExtra(Constant.KEY_PHONE, model.getPhone());
        intent.putExtra(Constant.KEY_EMAIL, model.getEmail());
        intent.putExtra(Constant.KEY_FCM_TOKEN, model.getFcmToken());
        intent.putExtra(Constant.KEY_STATUS, model.getStatus());
        intent.putExtra(Constant.KEY_WEBSITE, model.getWebsite());
        intent.putExtra(Constant.KEY_GENDER, model.getGender());
        if (model.getLatitude() != null) {
            intent.putExtra(Constant.KEY_LATITUDE, model.getLatitude());
        }
        if (model.getLongitude() != null) {
            intent.putExtra(Constant.KEY_LONGITUDE, model.getLongitude());
        }
    }

    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView) {
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }
    public static String timestampToString(Timestamp timestamp){
        return new SimpleDateFormat("HH:MM").format(timestamp.toDate());
    }
    public static void setImagePic(Context context, Uri imageUri, ImageView imageView) {
        Glide.with(context).load(imageUri).placeholder(R.drawable.baseline_user_24).into(imageView);
    }

    public static void checkLocationPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    public static boolean hasLocationPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static void loadCurrentProfilePicture(Context context, ImageView imageView) {
        if (context == null || imageView == null) return;

        FirebaseUtils.getCurrentProfilePicStorageRef()
                .getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri uri = task.getResult();
                        setProfilePic(context, uri, imageView);
                    } else {
                        // Có thể hiển thị ảnh mặc định nếu lỗi
                        imageView.setImageResource(R.drawable.baseline_user_24);
                    }
                });
    }
    public static void loadOtherProfilePicture(Context context, ImageView imageView, String otherUserId) {
        if (context == null || imageView == null || otherUserId == null || otherUserId.isEmpty()) return;

        FirebaseUtils.getOtherProfilePicStorageRef(otherUserId)
                .getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Glide.with(context)
                            .load(uri)
                            .placeholder(R.drawable.baseline_user_24)
                            .error(R.drawable.baseline_user_24)
                            .circleCrop()
                            .into(imageView);
                })
                .addOnFailureListener(e -> {
                    imageView.setImageResource(R.drawable.baseline_user_24);
                });
    }
    public static String getTimeAgo(Date date) {
        if (date == null) return "";
        long now = System.currentTimeMillis();
        long time = date.getTime();
        long diffInMillis = now - time;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(diffInMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
        long hours = TimeUnit.MILLISECONDS.toHours(diffInMillis);
        long days = TimeUnit.MILLISECONDS.toDays(diffInMillis);

        if (seconds < 60) {
            return "vừa xong";
        } else if (minutes < 60) {
            return minutes + " phút trước";
        } else if (hours < 24) {
            return hours + " giờ trước";
        } else if (days < 7) {
            return days + " ngày trước";
        } else if (days < 30) {
            return (days / 7) + " tuần trước";
        } else if (days < 365) {
            return (days / 30) + " tháng trước";
        } else {
            return (days / 365) + " năm trước";
        }
    }
    public static void alertdialog(AlertDialog dialog)
    {
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

}
