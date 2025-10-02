package com.example.project.Auth.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.project.MainActivity;
import com.example.project.Model.UserModel;
import com.example.project.Packages.Home.HomeMainActivity;
import com.example.project.R;
import com.example.project.Utils.AndroidUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class RegisterFragment extends Fragment {
    private FirebaseAuth auth;
    private TextInputEditText etEmail, enter_etPassword, etPassword;
    private MaterialButton btnRegister;


    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        auth = FirebaseAuth.getInstance();
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        enter_etPassword = view.findViewById(R.id.enter_etPassword);
        btnRegister = view.findViewById(R.id.btnRegister);
        register();
        return view;
    }

    private void register() {
        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = enter_etPassword.getText().toString().trim();
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                AndroidUtils.showToast(getContext(), "Vui lòng nhập đủ Email và Password");
                return;
            }
            if (password.length() < 6) {
                AndroidUtils.showToast(getContext(), "Mật khẩu phải có ít nhất 6 ký tự");
                return;
            }
            if (!password.equals(confirmPassword)) {
                AndroidUtils.showToast(getContext(), "Mật khẩu nhập lại không khớp");
                return;
            }
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity(), task -> {
                        if (task.isSuccessful()) {
                            saveUser();
                        } else {
                            AndroidUtils.showToast(getContext(), "Đăng ký thất bại: " + task.getException().getMessage());
                        }
                    });
        });
    }

    private void saveUser() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Tạo object UserModel
            UserModel userModel = new UserModel();
            userModel.setUserId(userId);
            userModel.setEmail(user.getEmail());
            userModel.setUsername(""); // để rỗng lúc đăng ký
            userModel.setPhone("");
            userModel.setGender("");
            userModel.setWebsite("");
            userModel.setStatus("Offline"); // offline mặc định

            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .set(userModel)
                    .addOnCompleteListener(storeTask -> {
                        if (storeTask.isSuccessful()) {
                            Intent intent = new Intent(getActivity(), HomeMainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            requireActivity().finish();
                        } else {
                            AndroidUtils.showToast(getContext(),
                                    "Không lưu được user: " + storeTask.getException().getMessage());
                        }
                    });
        } else {
            AndroidUtils.showToast(getContext(), "Lỗi: user null");
        }
    }

}