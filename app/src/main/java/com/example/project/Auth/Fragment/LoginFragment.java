package com.example.project.Auth.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.project.MainActivity;
import com.example.project.Packages.Home.HomeMainActivity;
import com.example.project.R;
import com.example.project.Utils.AndroidUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {
    private FirebaseAuth auth;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private ProgressBar progressBar;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        auth = FirebaseAuth.getInstance();
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        progressBar = view.findViewById(R.id.progress);
        login();
        return view;
    }


    private void login() {
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                AndroidUtils.showToast(getContext(), "Vui lòng nhập đủ Email và Password");
                return;
            }
            if (password.length() < 6) {
                AndroidUtils.showToast(getContext(), "Mật khẩu phải có ít nhất 6 ký tự");
                return;
            }
            btnLogin.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity(), task -> {
                        progressBar.setVisibility(View.GONE);
                        btnLogin.setVisibility(View.VISIBLE);

                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            AndroidUtils.showToast(getContext(), "Đăng nhập thành công");
                            Intent intent = new Intent(getActivity(), HomeMainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            requireActivity().finish();
                        } else {
                            AndroidUtils.showToast(getContext(),
                                    "Đăng nhập thất bại: " + task.getException().getMessage());
                        }
                    });
        });
    }

}