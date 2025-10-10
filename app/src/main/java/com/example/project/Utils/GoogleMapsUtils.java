package com.example.project.Utils;

import static com.example.project.Utils.AndroidUtils.showToast;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.project.Model.NewFeedModel;
import com.example.project.Model.UserModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoogleMapsUtils {
    public static final int FILE_PERMISSION_CODE = 1;
    /**
     * Lấy vị trí hiện tại của người dùng (dùng ở bất cứ Activity/Fragment nào)
     * @param context - context (dùng cho Toast)
     * @param activity - activity để request permission
     * @param callback - callback để nhận kết quả Location
     */

    public static void getCurrentLocation(Context context, Activity activity, LocationCallback callback) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FILE_PERMISSION_CODE);
            return;
        }

        FusedLocationProviderClient fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(context);

        fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        callback.onLocationReceived(location);
                    } else {
                        AndroidUtils.showToast(context, "Không thể lấy vị trí hiện tại (null)");
                        callback.onLocationError("Location is null");
                    }
                })
                .addOnFailureListener(e -> {
                    AndroidUtils.showToast(context, "Lỗi lấy vị trí: " + e.getMessage());
                    callback.onLocationError(e.getMessage());
                });
    }

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
    public static void updateLocationUserToFirestore( double lat, double lon, Context context){
        Map<String, Object> updates = new HashMap<>();
        updates.put(Constant.KEY_LATITUDE, lat);
        updates.put(Constant.KEY_LONGITUDE, lon);
        FirebaseUtils.currentUserDetails().update(updates)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()){
                        showToast(context, "Update thành công");

                    }else {
                        showToast(context, "Update thất bại");
                    }
                });
    }
    public interface LocationCallback {
        void onLocationReceived(Location location);
        void onLocationError(String error);
    }


    public static Task<List<UserModel>> getAllUsersAsync() {
        return FirebaseUtils.getAllUsers()
                .continueWith(task -> {
                    List<UserModel> users = new ArrayList<>();
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (var doc : task.getResult().getDocuments()) {
                            UserModel user = doc.toObject(UserModel.class);
                            if (user != null &&
                                    user.getLatitude() != null &&
                                    user.getLongitude() != null) {
                                users.add(user);
                            }
                        }
                    }
                    return users;
                });
    }
    public static List<UserModel> filterNearbyUsers(
            List<UserModel> allUsers, double myLat, double myLon, double radiusKm) {

        List<UserModel> nearby = new ArrayList<>();
        for (UserModel user : allUsers) {
            if (isWithinRadius(
                    myLat, myLon, user.getLatitude(), user.getLongitude(), radiusKm)) {
                nearby.add(user);
            }
        }
        return nearby;
    }
    public static boolean isWithinRadius(double myLat, double myLon,
                                         double targetLat, double targetLon,
                                         double radiusKm) {
        double distance = calculateDistance(myLat, myLon, targetLat, targetLon);
        return distance <= radiusKm;
    }
    public static Task<List<UserModel>> getNearbyUsersAsync(double myLat, double myLon, double radiusKm) {
        return getAllUsersAsync().continueWith(task -> {
            List<UserModel> allUsers = task.getResult();
            return filterNearbyUsers(allUsers, myLat, myLon, radiusKm);
        });
    }


    public static List<NewFeedModel> filterNearbyPosts(
            List<NewFeedModel> allPosts, double myLat, double myLon, double radiusKm) {

        List<NewFeedModel> nearbyPosts = new ArrayList<>();

        for (NewFeedModel post : allPosts) {
            if (post.getLatitude() != 0 && post.getLongitude() != 0) {
                if (isWithinRadius(
                        myLat, myLon, post.getLatitude(), post.getLongitude(), radiusKm)) {
                    nearbyPosts.add(post);
                }
            }
        }

        return nearbyPosts;
    }
    public static Task<List<NewFeedModel>> getNearbyPostsAsync(double myLat, double myLon, double radiusKm) {
        return FirebaseUtils.postStory()
                .get()
                .continueWith(task -> {
                    List<NewFeedModel> allPosts = new ArrayList<>();
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (var doc : task.getResult().getDocuments()) {
                            NewFeedModel post = doc.toObject(NewFeedModel.class);
                            if (post != null) allPosts.add(post);
                        }
                    }
                    return filterNearbyPosts(allPosts, myLat, myLon, radiusKm);
                });
    }
}
