package com.example.project.Packages.User.Profile.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.project.Model.UserModel;
import com.example.project.R;
import com.example.project.Utils.AndroidUtils;
import com.example.project.Utils.FirebaseUtils;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class AddProfileDialogFragment extends DialogFragment {
    private MaterialAutoCompleteTextView genderAutoComplete;
    private CircleImageView avatar;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectdImageUri;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();
    ProgressBar progressBar, progress_data;
    UserModel currentUserModel;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUserModel = new UserModel();


        imagePickLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectdImageUri = data.getData();
                            AndroidUtils.setProfilePic(requireContext(), selectdImageUri, avatar);
                        }
                    }
                }
        );
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view = getLayoutInflater().inflate(R.layout.fragment_add_profile, null);
        TextInputEditText email = view.findViewById(R.id.email);
        TextInputEditText phone = view.findViewById(R.id.phone);
        TextInputEditText website = view.findViewById(R.id.website);
        TextView name = view.findViewById(R.id.name);
        genderAutoComplete = view.findViewById(R.id.genderAutoComplete);
        avatar = view.findViewById(R.id.avatar);

        progress_data = view.findViewById(R.id.progress_data);
        progressBar = view.findViewById(R.id.profile_progress_bar);
        String[] genders = {"Nam", "Nữ"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, genders);
        genderAutoComplete.setAdapter(adapter);
        avatar.setOnClickListener(view13 -> {
            ImagePicker.with(this).
                    cropSquare().compress(512).
                    maxResultSize(512, 512).
                    createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickLauncher.launch(intent);
                            return null;
                        }
                    });


        });
        getUserData(email, phone, website, name);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view)
                .setTitle("Thêm thông tin")
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String username = name.getText().toString();
                    String emailText = email.getText().toString();
                    String phoneText = phone.getText().toString();
                    String websiteText = website.getText().toString();
                    String genderText = genderAutoComplete.getText().toString();
                    if(username.isEmpty() || username.length()<2){
                        AndroidUtils.showToast(getContext(),"Vui lòng nhập tên hợp lệ");
                        return;
                    }if (phoneText.isEmpty() || phoneText.length() < 10 || !phoneText.matches("\\d+")){
                        AndroidUtils.showToast(getContext(),"Vui lòng nhập số điện thoại hợp lệ");
                        return;

                    }
                    else{
                        currentUserModel.setUsername(username);
                        currentUserModel.setEmail(emailText);
                        currentUserModel.setPhone(phoneText);
                        currentUserModel.setWebsite(websiteText);
                        currentUserModel.setGender(genderText);
                        setInProgressData(true);
                        if (selectdImageUri != null){
                            FirebaseUtils.getCurrentProfilePicStorageRef().putFile(selectdImageUri).
                                    addOnCompleteListener(task -> {
                                        updateToFireSore();
                                    });
                        }else {
                            updateToFireSore();
                        }


                    }


                })
                .setNegativeButton("Hủy", (dialog, which) -> dismiss());

        return builder.create();

    }

    void getUserData( TextInputEditText email, TextInputEditText phone,   TextInputEditText website, TextView name ){
        setInProgress(true);
        if (currentUser != null) {
            boolean hasEmail = currentUser.getEmail() != null;

            if (hasEmail) {
                email.setText(currentUser.getEmail());
            }  else {
                Log.d("Auth", "NO email no phone");
            }
        }
        FirebaseUtils.currentUserDetails().get().addOnCompleteListener(task -> {
            setInProgress(false);
            currentUserModel = task.getResult().toObject(UserModel.class);
            if (currentUser != null) {
                name.setText(currentUserModel.getUsername());
                phone.setText(currentUserModel.getPhone());

                website.setText(currentUserModel.getWebsite());
                genderAutoComplete.setText(currentUserModel.getGender());
            }
        });
        FirebaseUtils.getCurrentProfilePicStorageRef().getDownloadUrl().
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()){

                        Uri uri = task.getResult();
                        AndroidUtils.setProfilePic(getContext(), uri, avatar);
                    }

                });
    }
    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = (int) (requireContext().getResources().getDisplayMetrics().widthPixels * 0.9); // 90% màn hình
            getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);

            getDialog().getWindow().setBackgroundDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.dialog_backgroud)
            );
        }
    }

    void updateToFireSore(){
        FirebaseUtils.currentUserDetails().set(currentUserModel)
                .addOnCompleteListener(task -> {
                    if (!isAdded()) return;
                    setInProgress(false);
                    if (task.isSuccessful()){
                        AndroidUtils.showToast(requireContext(), "Update thành công");

                    }else {
                        AndroidUtils.showToast(requireContext(), "Update thất bại");
                    }
                });
    }
    void setInProgress(boolean inProgress){
        if (inProgress){
            progressBar.setVisibility(View.VISIBLE);

        }else {
            progressBar.setVisibility(View.GONE);

        }
    }
    void setInProgressData(boolean inProgress){

        if (inProgress){
            progress_data.setVisibility(View.VISIBLE);
        }else {
            progress_data.setVisibility(View.GONE);
        }
    }
}
