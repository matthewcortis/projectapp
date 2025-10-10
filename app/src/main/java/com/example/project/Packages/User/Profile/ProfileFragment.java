package com.example.project.Packages.User.Profile;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.project.Auth.MainAuthActivity;
import com.example.project.Model.PreferenceManager;
import com.example.project.Model.UserModel;
import com.example.project.Packages.User.Profile.Fragment.AddProfileDialogFragment;
import com.example.project.R;
import com.example.project.Utils.AndroidUtils;
import com.example.project.Utils.FirebaseUtils;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {
    CircleImageView avatar;
    PreferenceManager preferenceManager;
    UserModel currentUserModel;
    AppCompatImageView addProfileBtn;
    TextView name, logoutBtn;
    TextInputEditText email, phone, website, gender;
    private ProgressBar progressBar;
    private TextInputLayout email_layout, phone_layout, website_layout, genderTextInputLayout;
    public ProfileFragment() {

    }
    private void getData() {
        setLoading(true);

        FirebaseUtils.currentUserDetails().get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                currentUserModel = task.getResult().toObject(UserModel.class);
                if (currentUserModel != null) {
                    name.setText(currentUserModel.getUsername());
                    email.setText(currentUserModel.getEmail());
                    phone.setText(currentUserModel.getPhone());
                    website.setText(currentUserModel.getWebsite());
                    gender.setText(currentUserModel.getGender());
                }
            } else {
                AndroidUtils.showToast(getContext(), "Không tải được thông tin user");
            }
            AndroidUtils.loadCurrentProfilePicture(requireContext(), avatar);
        });
        setLoading(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        preferenceManager = new PreferenceManager(requireContext());
        addProfileBtn = view.findViewById(R.id.AddProfileBtn);
        email = view.findViewById(R.id.email);
        gender = view.findViewById(R.id.gender);
        phone = view.findViewById(R.id.phone);
        website = view.findViewById(R.id.website);
        name = view.findViewById(R.id.name);
        logoutBtn = view.findViewById(R.id.logout_btn);
        avatar = view.findViewById(R.id.avatar);
        progressBar = view.findViewById(R.id.progress);
        email_layout = view.findViewById(R.id.email_layout);
        phone_layout = view.findViewById(R.id.phone_layout);
        website_layout = view.findViewById(R.id.website_layout);
        genderTextInputLayout = view.findViewById(R.id.genderTextInputLayout);

        getData();
        addProfile();
        logoutBtn.setOnClickListener(v -> {
            sigOutDialog();
        });
        return view;
    }

    private void addProfile() {
        addProfileBtn.setOnClickListener(v -> {
            AddProfileDialogFragment dialog = new AddProfileDialogFragment();
            dialog.show(getParentFragmentManager(), "AddProfileDialog");
        });

    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            avatar.setVisibility(View.INVISIBLE);
            name.setVisibility(View.INVISIBLE);
            email_layout.setVisibility(View.INVISIBLE);
            phone_layout.setVisibility(View.INVISIBLE);
            website_layout.setVisibility(View.INVISIBLE);
            genderTextInputLayout.setVisibility(View.INVISIBLE);
            addProfileBtn.setVisibility(View.INVISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            // Hiện lại UI sau khi load xong
            avatar.setVisibility(View.VISIBLE);
            name.setVisibility(View.VISIBLE);
            email_layout.setVisibility(View.VISIBLE);
            phone_layout.setVisibility(View.VISIBLE);
            website_layout.setVisibility(View.VISIBLE);
            genderTextInputLayout.setVisibility(View.VISIBLE);
            addProfileBtn.setVisibility(View.VISIBLE);
        }
    }

    void sigOutDialog(){
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_dialog_sign_out);
        Drawable customBackground  = ContextCompat.getDrawable(getContext(), R.drawable.dialog_backgroud);
        dialog.getWindow().setBackgroundDrawable(customBackground);
        TextView no = dialog.findViewById(R.id.cancelBtn);
        TextView yes = dialog.findViewById(R.id.successBtn);

        no.setOnClickListener(v -> {
            dialog.dismiss();
        });
        yes.setOnClickListener(v -> {

            dialog.dismiss();
            preferenceManager.clear();
            FirebaseUtils.logout();
            Intent intent = new Intent(getContext(), MainAuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        dialog.show();

    }
}