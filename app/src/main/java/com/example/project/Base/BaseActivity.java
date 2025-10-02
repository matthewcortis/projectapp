package com.example.project.Base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project.Utils.FirebaseUtils;
import com.google.firebase.firestore.DocumentReference;

public class BaseActivity extends AppCompatActivity {
    private DocumentReference documentReference;
    private final String UserId = FirebaseUtils.currentUserID();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (UserId == null){
            return;
        }
        documentReference = FirebaseUtils.status(UserId);
    }

    @Override
    protected void onPause() {
        super.onPause();

        documentReference.update("status", "0");
    }

    @Override
    protected void onResume() {
        super.onResume();

        documentReference.update("status", "1");
    }
}
