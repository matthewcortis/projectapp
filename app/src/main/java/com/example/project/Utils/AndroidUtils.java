package com.example.project.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.project.Model.UserModel;
import com.example.project.R;

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
    public static void setImagePic(Context context, Uri imageUri, ImageView imageView){
        Glide.with(context).load(imageUri).placeholder(R.drawable.baseline_user_24).into(imageView);
    }
    // Hàm kiểm tra và yêu cầu quyền
    public static void checkLocationPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Chưa cấp quyền -> yêu cầu quyền
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    public static boolean hasLocationPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }
}
