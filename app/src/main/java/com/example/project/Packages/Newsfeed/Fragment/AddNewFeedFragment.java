package com.example.project.Packages.Newsfeed.Fragment;

import static com.example.project.Utils.AndroidUtils.showToast;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.project.Model.NewFeedModel;
import com.example.project.Model.UserModel;
import com.example.project.Packages.Home.HomeMainActivity;
import com.example.project.R;
import com.example.project.Utils.AndroidUtils;
import com.example.project.Utils.Constant;
import com.example.project.Utils.FirebaseUtils;
import com.example.project.Utils.GoogleMapsUtils;
import com.example.project.Utils.Permission;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.units.qual.A;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class AddNewFeedFragment extends Fragment {
    LinearLayout locationBtn;
    Dialog dialog;
    String mimeType;
    HashMap<String, Object> myUser;
    FusedLocationProviderClient fusedLocationClient;
    TextView address, city, country, lagitude, longitude, successBtn, cancelBtn, location, postBtn;
    UserModel currentUserModel;
    EditText writeStory;
    ProgressBar progressBar;
    FloatingActionButton addImagePostBtn;
    CardView imageBackgroundCardView, videoBackgroundCardView;
    private Uri ImageUri, videoUri, mediaUri;
    VideoView videoStory;
    ImageView imagePost,backBtn;
    Permission permission;
    private final static int REQUEST_CODE = 100;

    public AddNewFeedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUserModel = new UserModel();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_new_feed, container, false);
        locationBtn = view.findViewById(R.id.locationBtn);
        location = view.findViewById(R.id.locationTxt);
        imageBackgroundCardView = view.findViewById(R.id.imageBackgroundCardView);
        addImagePostBtn = view.findViewById(R.id.add_image);
        imagePost = view.findViewById(R.id.image);
        videoStory = view.findViewById(R.id.videoViewStory);
        backBtn = view.findViewById(R.id.back_btn);
        videoBackgroundCardView = view.findViewById(R.id.videoBackgroundCardView);
        postBtn = view.findViewById(R.id.post);
        writeStory = view.findViewById(R.id.writeStoryPost);
        setData();
        backBtn.setOnClickListener(v -> {
            ((HomeMainActivity) requireActivity()).setBottomNavVisible(true);
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        addImagePostBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*, video/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });

        postBtn.setOnClickListener(v -> {
            if (mediaUri == null) {
                AndroidUtils.showToast(requireContext(), "Bạn chưa chọn ảnh hoặc video");
                return;
            }
            if (writeStory.getText().toString().trim().isEmpty()) {
                AndroidUtils.showToast(requireContext(), "Bạn chưa nhập nội dung");
                return;
            }

            mimeType = requireContext().getContentResolver().getType(mediaUri);
            if (mimeType != null && mimeType.startsWith("video/")) {
                try {
                    Video(videoUri);
                } catch (IOException e) {
                    AndroidUtils.showToast(requireContext(), "Lỗi video: " + e.getMessage());
                }
            } else if (mimeType != null && mimeType.startsWith("image/")) {
                Image(ImageUri);
            } else {
                AndroidUtils.showToast(requireContext(), "Lỗi định dạng");
            }
            ((HomeMainActivity) requireActivity()).setBottomNavVisible(true);
            requireActivity().getSupportFragmentManager().popBackStack();
        });
        locationBtn.setOnClickListener(v -> {
            showLocationDialog();
        });

        return view;
    }


    private void sendVideoMessage(String mediaUrl) {
        if (writeStory.getText().toString().isEmpty()){
            AndroidUtils.showToast( requireContext(),"Hãy viết gì đó hoặc chọn ảnh để tạo bài viết mới!");
        }else {
            String addressText = (address != null && address.getText() != null)
                    ? address.getText().toString().trim()
                    : "";
            double lat = currentUserModel.getLatitude();
            double lon = currentUserModel.getLongitude();
            NewFeedModel post = new NewFeedModel(
                    lon,                       // longitude
                    lat,                       // latitude
                    0.0,                       // rating
                    new Date(),                // postTimestamp
                    0,                         // likeCount
                    addressText,                // location
                    "0",                       // videoStatus
                    "1",                       // imageStatus
                    writeStory.getText().toString().trim(), // postText
                    mediaUrl,                  // postMedia
                    currentUserModel.getUserId() // idAuthor
            );
            postNewFeed(post);
        }


    }
    private void sendImageMessage(String mediaUrl) {
        String text = writeStory.getText().toString().trim();

        if (text.isEmpty() && (mediaUrl == null || mediaUrl.isEmpty())) {
            AndroidUtils.showToast(requireContext(),
                    "Hãy viết gì đó hoặc chọn ảnh để tạo bài viết mới!");
            return;
        }
        String addressText = (address != null && address.getText() != null)
                ? address.getText().toString().trim()
                : "";
        double lat = currentUserModel.getLatitude() != null ? currentUserModel.getLatitude() : 0.0;
        double lon = currentUserModel.getLongitude() != null ? currentUserModel.getLongitude() : 0.0;
        NewFeedModel post = new NewFeedModel(
                lon,                       // longitude
                lat,                       // latitude
                0.0,                       // rating
                new Date(),                // postTimestamp
                0,                         // likeCount
                addressText,               // location
                "0",                       // videoStatus
                "1",                       // imageStatus
                writeStory.getText().toString().trim(), // postText
                mediaUrl,                  // postMedia
                currentUserModel.getUserId() // idAuthor
        );

        postNewFeed(post);
    }


    private void postNewFeed(NewFeedModel newFeedModel){
        FirebaseUtils.postStory()
                .add(newFeedModel)
                .addOnSuccessListener(documentReference -> {
                    if (isAdded() && getContext() != null) {
                        AndroidUtils.showToast(getContext(), "Bài viết đã được đăng lên trang cá nhân");
                        requireActivity().onBackPressed();
                    }
                })
                .addOnFailureListener(e -> {
                    if (isAdded() && getContext() != null) {
                        AndroidUtils.showToast(getContext(), e.getMessage());
                    }
                });
    }
    void setData(){
        FirebaseUtils.currentUserDetails().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    myUser = new HashMap<>();
                    myUser.put(Constant.KEY_USER_ID, document.getString(Constant.KEY_USER_ID));
                    myUser.put(Constant.KEY_USER_NAME, document.getString(Constant.KEY_USER_NAME));
                    myUser.put(Constant.KEY_PHONE, document.getString(Constant.KEY_PHONE));
                    currentUserModel = new UserModel(myUser);


                } else {
                    AndroidUtils.showToast(requireContext(), "Error");

                }
            }
        });
    }
    private final ActivityResultLauncher<Intent> pickImage =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (result.getData() != null) {
                        mediaUri = result.getData().getData();
                        try {
                            mimeType = requireContext().getContentResolver().getType(mediaUri);

                            if (mimeType != null && mimeType.startsWith("video/")) {
                                videoBackgroundCardView.setVisibility(View.VISIBLE);
                                imageBackgroundCardView.setVisibility(View.GONE);

                                videoUri = mediaUri;
                                videoStory.setVideoURI(mediaUri);
                                videoStory.start();

                            } else if (mimeType != null && mimeType.startsWith("image/")) {
                                imageBackgroundCardView.setVisibility(View.VISIBLE);
                                videoBackgroundCardView.setVisibility(View.GONE);

                                AndroidUtils.setImagePic(requireContext(), mediaUri, imagePost);
                                ImageUri = mediaUri;

                            } else {
                                Log.e("MediaPicker", "Tệp không phải là ảnh hoặc video");
                            }
                        } catch (Exception e) {
                            Log.e("FilePicker", "Lỗi khi xử lý tệp", e);
                        }
                    }
                }
            });

    private void Image(Uri imageUri){
        if (imageUri != null){
            UploadTask uploadTask = FirebaseUtils.putMediaImagesStory().putFile(imageUri);
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){
                        task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                sendImageMessage(imageUrl);


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showToast(requireContext(), "Failed get URL");
                            }
                        });
                    }else {
                        AndroidUtils.showToast(requireContext(), "tải image thất bại");
                    }
                }
            });
        }else {
            sendImageMessage("null");

        }
    }
    private void Video(Uri videoUri) throws IOException {
        if (videoUri != null){
            try {
                long fileSize = permission.getFileSize(requireContext(), videoUri);
                long maxFileSize = 10 * 1024 * 1024; // 10MB

                if (fileSize > maxFileSize) {
                    showToast(requireContext(), "File size exceeds the limit");
                    return;
                }
                UploadTask uploadTask = FirebaseUtils.putMediaVideosStory().putFile(videoUri);
                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                    }
                });
                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String videoUrl = uri.toString();
                                    sendVideoMessage(videoUrl);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    AndroidUtils.showToast(requireContext(), "Failed get URL");
                                }
                            });
                        }else {
                            AndroidUtils.showToast(requireContext(), "tải video thất bại");
                        }
                    }
                });
            }catch (Exception e){
                AndroidUtils.showToast(requireContext(), "tải video thất bại");
            }
        }else {
            AndroidUtils.showToast(requireContext(), "tải video thất bại");
        }
    }
    private void showLocationDialog() {
        dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.custom_dialog_get_location);
        Drawable customBackground = ContextCompat.getDrawable(requireContext(), R.drawable.dialog_backgroud);
        dialog.getWindow().setBackgroundDrawable(customBackground);
        successBtn = dialog.findViewById(R.id.successBtn);
        progressBar = dialog.findViewById(R.id.progress_data);
        cancelBtn = dialog.findViewById(R.id.cancelBtn);
        address = dialog.findViewById(R.id.address);
        city = dialog.findViewById(R.id.city);
        country = dialog.findViewById(R.id.country);
        lagitude = dialog.findViewById(R.id.lagitude);
        longitude = dialog.findViewById(R.id.longitude);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());


        successBtn.setOnClickListener(v -> {
            location.setText(address.getText().toString());

            dialog.dismiss();
        });
        cancelBtn.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            progressBar.setVisibility(View.VISIBLE);
            checkLocationPermissionAndFetch();
        } else {
            askForLocationPermission();
        }



    }
    private void checkLocationPermissionAndFetch() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fetchLocation();
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }
    }


    @SuppressLint("MissingPermission")
    private void fetchLocation() {
        progressBar.setVisibility(View.VISIBLE); // Bật ProgressBar
        fusedLocationClient.getCurrentLocation(com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {
                    progressBar.setVisibility(View.GONE);
                    if (location != null) {
                        fillLocationData(location);
                    } else {
                        showToast(requireContext(), "Không lấy được vị trí, thử lại");
                    }
                }).addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    showToast(requireContext(), "Lỗi khi lấy vị trí");
                });
    }
    private void fillLocationData(Location loc) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address addr = addresses.get(0);
                double lagg = addr.getLatitude();
                double longg = addr.getLongitude();
                lagitude.setText(String.valueOf(lagg));
                longitude.setText(String.valueOf(longg));
                currentUserModel.setLatitude(lagg);
                currentUserModel.setLongitude(longg);

                String fullAddress = addr.getAddressLine(0);
                if (fullAddress != null && fullAddress.contains(" ")) {
                    fullAddress = fullAddress.substring(fullAddress.indexOf(" ") + 1);
                }
                address.setText(fullAddress);
                city.setText(addr.getLocality());
                country.setText(addr.getCountryName());
                updateLocationToFirestore(lagg, longg);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showToast(requireContext(), "Không thể lấy địa chỉ");
        }
    }
    void updateLocationToFirestore(double lat, double lon){
        if (!isAdded()) return;
        GoogleMapsUtils.updateLocationUserToFirestore(lat, lon, requireContext());
    }
    private void askForLocationPermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation();
            }else {
                showToast(requireActivity(), "Permission denied");
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}