package com.example.project.Packages.User.Profile;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.project.Model.UserModel;
import com.example.project.Packages.User.Profile.Fragment.AddProfileDialogFragment;
import com.example.project.R;
import com.example.project.Utils.AndroidUtils;
import com.example.project.Utils.FirebaseUtils;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {
    CircleImageView avatar;
    UserModel currentUserModel;
    AppCompatImageView addProfileBtn;
    TextView name;
    TextInputEditText email, phone, website, gender;
    public ProfileFragment() {

    }
    private void getData(){
        FirebaseUtils.currentUserDetails().get().addOnCompleteListener(task -> {
            currentUserModel = task.getResult().toObject(UserModel.class);
            assert currentUserModel != null;
            name.setText(currentUserModel.getUsername());
            email.setText(currentUserModel.getEmail());
            phone.setText(currentUserModel.getPhone());
            website.setText(currentUserModel.getWebsite());
            gender.setText(currentUserModel.getGender());


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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        addProfileBtn = view.findViewById(R.id.AddProfileBtn);
        email = view.findViewById(R.id.email);
        gender = view.findViewById(R.id.gender);
        phone = view.findViewById(R.id.phone);
        website = view.findViewById(R.id.website);
        name = view.findViewById(R.id.name);
        avatar = view.findViewById(R.id.avatar);

        getData();
        addProfile();
        return view;
    }

    private void addProfile() {
        addProfileBtn.setOnClickListener(v -> {
            AddProfileDialogFragment dialog = new AddProfileDialogFragment();
            dialog.show(getParentFragmentManager(), "AddProfileDialog");
        });

    }

}