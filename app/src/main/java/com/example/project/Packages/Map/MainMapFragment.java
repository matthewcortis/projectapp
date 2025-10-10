package com.example.project.Packages.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.project.Model.UserModel;
import com.example.project.Packages.Chat.Room.ChatRoomActivity;
import com.example.project.R;
import com.example.project.Utils.AndroidUtils;
import com.example.project.Utils.FirebaseUtils;
import com.example.project.Utils.GoogleMapsUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputEditText;

public class MainMapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private final int FILE_PERMISSION_CODE = 1;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;


    public MainMapFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main_map, container, false);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        getCurrentLocation();
        return view;
    }


    private void getCurrentLocation() {
        GoogleMapsUtils.getCurrentLocation(requireContext(), requireActivity(), new GoogleMapsUtils.LocationCallback(){
            @Override
            public void onLocationReceived(Location location) {
                currentLocation = location;
                initMap();
            }
            @Override
            public void onLocationError(String error) {
                AndroidUtils.showToast(requireContext(), "Lỗi vị trí: " + error);
            }
        });
    }
    private void addUserMarkerWithAvatar(UserModel user, LatLng position) {
        FirebaseUtils.getOtherProfilePicStorageRef(user.getUserId())
                .getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Glide.with(requireContext())
                            .asBitmap()
                            .load(uri)
                            .circleCrop()
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource,
                                                            @Nullable Transition<? super Bitmap> transition) {
                                    Bitmap smallBitmap = Bitmap.createScaledBitmap(resource, 90, 90, false);
                                    Marker marker = mMap.addMarker(new MarkerOptions()
                                            .position(position)
                                            .icon(BitmapDescriptorFactory.fromBitmap(smallBitmap))
                                            .title(user.getUsername()));
                                    marker.setTag(user);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {}
                            });
                })
                .addOnFailureListener(e -> {
                    Glide.with(requireContext())
                            .asBitmap()
                            .load(R.drawable.baseline_user_24)
                            .circleCrop()
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource,
                                                            @Nullable Transition<? super Bitmap> transition) {
                                    Bitmap smallBitmap = Bitmap.createScaledBitmap(resource, 90, 90, false);
                                    Marker marker = mMap.addMarker(new MarkerOptions()
                                            .position(position)
                                            .icon(BitmapDescriptorFactory.fromBitmap(smallBitmap))
                                            .title(user.getUsername()));
                                    marker.setTag(user);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {}
                            });

                });
    }
    private void initMap() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            AndroidUtils.showToast(requireContext(), "Không tìm thấy Map Fragment (R.id.map)");
        }
    }
    private void showUserDialog(UserModel user) {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.custom_dialog_user_popup, null);

        ImageView imgAvatar = dialogView.findViewById(R.id.user_avatar);
        TextView txtName = dialogView.findViewById(R.id.user_name);
        Button btnMessage = dialogView.findViewById(R.id.view_profile_btn);
        ImageButton btnCloseDialog = dialogView.findViewById(R.id.btnCloseDialog);
        TextInputEditText inputEmail = dialogView.findViewById(R.id.inputEmail);
        TextInputEditText inputPhone = dialogView.findViewById(R.id.inputPhone);
        TextInputEditText inputWebsite = dialogView.findViewById(R.id.inputWebsite);
        TextInputEditText inputGender = dialogView.findViewById(R.id.inputGender);
        inputEmail.setEnabled(false);
        inputPhone.setEnabled(false);
        inputWebsite.setEnabled(false);
        inputGender.setEnabled(false);
        AndroidUtils.loadOtherProfilePicture(requireContext(), imgAvatar, user.getUserId());
        inputEmail.setText(user.getEmail());
        inputPhone.setText(user.getPhone());
        inputWebsite.setText(user.getWebsite());
        inputGender.setText(user.getGender());
        txtName.setText(user.getUsername());
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        btnMessage.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(requireContext(), ChatRoomActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("otherUser", user);
            startActivity(intent);
        });
        btnCloseDialog.setOnClickListener(v -> dialog.dismiss());
        AndroidUtils.alertdialog(dialog);
        dialog.show();
    }
    private void loadNearbyUsers(double myLat, double myLon) {
        double radiusKm = 5.0; // bán kính 5km
        GoogleMapsUtils.getNearbyUsersAsync(myLat, myLon, radiusKm)
                .addOnSuccessListener(nearbyUsers -> {
                    if (nearbyUsers.isEmpty()) {
                        AndroidUtils.showToast(requireContext(), "Không có người nào gần bạn");
                        return;
                    }

                    for (UserModel user : nearbyUsers) {
                        LatLng pos = new LatLng(user.getLatitude(), user.getLongitude());
                        addUserMarkerWithAvatar(user, pos);
                    }
                })
                .addOnFailureListener(e ->
                        AndroidUtils.showToast(requireContext(), "Lỗi tải user: " + e.getMessage()));
    }
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng myLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        mMap.clear();
        mMap.addCircle(new CircleOptions()
                .center(myLatLng)
                .radius(5000) // mét
                .strokeColor(Color.BLUE)
                .fillColor(0x220000FF)
                .strokeWidth(2));

        float fixedZoom = 14f;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, fixedZoom));

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setOnMarkerClickListener(marker -> {
            if (marker.getTag() instanceof UserModel) {
                showUserDialog((UserModel) marker.getTag());
            }
            return true;
        });
        loadNearbyUsers(currentLocation.getLatitude(), currentLocation.getLongitude());

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FILE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                AndroidUtils.showToast(requireContext(), "Từ chối quyền truy cập vị trí");
            }
        }
    }





}