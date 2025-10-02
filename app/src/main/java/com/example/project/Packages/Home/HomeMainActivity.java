package com.example.project.Packages.Home;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.project.Base.BaseActivity;
import com.example.project.Model.PreferenceManager;
import com.example.project.Packages.Chat.ChatFragment;
import com.example.project.Packages.Map.MainMapFragment;
import com.example.project.Packages.Newsfeed.NewFeedFragment;
import com.example.project.Packages.User.Profile.ProfileFragment;
import com.example.project.R;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class HomeMainActivity extends BaseActivity {
    private PreferenceManager preferenceManager;
    BottomNavigationView bottomNavigationView;
    DrawerLayout drawerLayout;
    ProfileFragment profileFragment;
    NavigationView navigationView;
    Context context;
    NewFeedFragment newFeedFragment;
    ChatFragment chatFragment;
    ImageButton menuBtn;
    MainMapFragment mainMapFragment;




    private void navigationView(){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (drawerLayout != null){
                    drawerLayout.closeDrawers();
                }
                if (item.getItemId() == R.id.navChat){
                    displayFragment(chatFragment);

                }
                return true;
            }
        });
    }

    private void bottomNavigationView(){
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.menu_profile){
                    displayFragment(profileFragment);
                }
                if (item.getItemId() == R.id.menu_story){
                    displayFragment(newFeedFragment);

                }
                if (item.getItemId() == R.id.menu_chat){
                    displayFragment(chatFragment);

                }
                if (item.getItemId() == R.id.menu_map){
                    displayFragment(mainMapFragment);

                }
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_chat);
    }

    private void displayFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, fragment);
        transaction.hide(profileFragment);
        transaction.hide(newFeedFragment);
        transaction.hide(chatFragment);
        transaction.hide(mainMapFragment);
        transaction.show(fragment);
        transaction.commit();
    }

    // Thông báo nếu có tin nhắn mới
    void getChatRequest_Badge(){
        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.menu_chat);
        badgeDrawable.setVerticalOffset(5);
        badgeDrawable.setHorizontalOffset(5);
        badgeDrawable.setBackgroundColor(Color.RED);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawrlayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        context = this;
        preferenceManager =new PreferenceManager(context);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        drawerLayout =findViewById(R.id.drawrlayout);
        navigationView = findViewById(R.id.navigationview);
        menuBtn = findViewById(R.id.menuBtn);
        menuBtn.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));
        newFeedFragment = new NewFeedFragment();
        profileFragment = new ProfileFragment();
        chatFragment = new ChatFragment();
        mainMapFragment = new MainMapFragment();
        bottomNavigationView();
        navigationView();

    }
}