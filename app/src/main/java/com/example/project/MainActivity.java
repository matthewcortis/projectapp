package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.project.Auth.MainAuthActivity;
import com.example.project.Model.PreferenceManager;
import com.example.project.Model.UserModel;
import com.example.project.Packages.Home.HomeMainActivity;
import com.example.project.Utils.AndroidUtils;
import com.example.project.Utils.Constant;
import com.example.project.Utils.FirebaseUtils;
import com.google.firebase.firestore.DocumentSnapshot;
public class MainActivity extends AppCompatActivity {
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        preferenceManager = new PreferenceManager(getApplicationContext());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (FirebaseUtils.isLoggedIn() && getIntent().getExtras() != null) {
            String userId = FirebaseUtils.currentUserID();
            FirebaseUtils.allUserCollectionReference().document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            preferenceManager.putString(Constant.KEY_USER_ID, documentSnapshot.getId());
                            preferenceManager.putString(Constant.KEY_USER_NAME, documentSnapshot.getString(Constant.KEY_USER_NAME));
                            preferenceManager.putString(Constant.KEY_PHONE, documentSnapshot.getString(Constant.KEY_PHONE));
                            preferenceManager.putString(Constant.KEY_EMAIL, documentSnapshot.getString(Constant.KEY_EMAIL));
                            preferenceManager.putString(Constant.KEY_FCM_TOKEN, documentSnapshot.getString(Constant.KEY_FCM_TOKEN));
                            preferenceManager.putString(Constant.KEY_STATUS, documentSnapshot.getString(Constant.KEY_STATUS));
                            preferenceManager.putString(Constant.KEY_WEBSITE, documentSnapshot.getString(Constant.KEY_WEBSITE));
                            preferenceManager.putString(Constant.KEY_GENDER, documentSnapshot.getString(Constant.KEY_GENDER));

                            UserModel model = documentSnapshot.toObject(UserModel.class);

                            Intent intent = new Intent(this, HomeMainActivity.class);
                            AndroidUtils.passUserModelAsIntent(intent, model);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });
        } else {
            new Handler().postDelayed(() -> {
                if (FirebaseUtils.isLoggedIn()) {
                    String userId = FirebaseUtils.currentUserID();
                    FirebaseUtils.allUserCollectionReference().document(userId).get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    preferenceManager.putString(Constant.KEY_USER_ID, documentSnapshot.getId());
                                    preferenceManager.putString(Constant.KEY_USER_NAME, documentSnapshot.getString(Constant.KEY_USER_NAME));
                                    preferenceManager.putString(Constant.KEY_PHONE, documentSnapshot.getString(Constant.KEY_PHONE));
                                    preferenceManager.putString(Constant.KEY_EMAIL, documentSnapshot.getString(Constant.KEY_EMAIL));
                                    preferenceManager.putString(Constant.KEY_FCM_TOKEN, documentSnapshot.getString(Constant.KEY_FCM_TOKEN));
                                    preferenceManager.putString(Constant.KEY_STATUS, documentSnapshot.getString(Constant.KEY_STATUS));
                                    preferenceManager.putString(Constant.KEY_WEBSITE, documentSnapshot.getString(Constant.KEY_WEBSITE));
                                    preferenceManager.putString(Constant.KEY_GENDER, documentSnapshot.getString(Constant.KEY_GENDER));
                                }
                            });
                } else {
                    startActivity(new Intent(MainActivity.this, MainAuthActivity.class));
                }
                finish();
            }, 1500);
        }
    }
}
