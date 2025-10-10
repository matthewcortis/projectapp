package com.example.project.Packages.Home;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.project.Base.BaseActivity;
import com.example.project.Model.PreferenceManager;
import com.example.project.Packages.Chat.ChatFragment;
import com.example.project.Packages.Map.MainMapFragment;
import com.example.project.Packages.Newsfeed.NewFeedFragment;
import com.example.project.Packages.User.Profile.ProfileFragment;
import com.example.project.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class HomeMainActivity extends BaseActivity {

    private PreferenceManager preferenceManager;
    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Context context;

    private ProfileFragment profileFragment;
    private NewFeedFragment newFeedFragment;
    private ChatFragment chatFragment;
    private MainMapFragment mainMapFragment;
    private Fragment activeFragment;
    private FragmentManager fm;
    private AppBarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_main);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawrlayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        context = this;
        preferenceManager = new PreferenceManager(context);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        drawerLayout = findViewById(R.id.drawrlayout);
        navigationView = findViewById(R.id.navigationview);
        appBarLayout = findViewById(R.id.appbar_layout);

        profileFragment = new ProfileFragment();
        newFeedFragment = new NewFeedFragment();
        chatFragment = new ChatFragment();
        mainMapFragment = new MainMapFragment();

        fm = getSupportFragmentManager();
        activeFragment = chatFragment;

        fm.beginTransaction()
                .add(R.id.main_frame_layout, profileFragment, "profile").hide(profileFragment)
                .commit();
        fm.beginTransaction()
                .add(R.id.main_frame_layout, newFeedFragment, "feed").hide(newFeedFragment)
                .commit();
        fm.beginTransaction()
                .add(R.id.main_frame_layout, mainMapFragment, "map").hide(mainMapFragment)
                .commit();
        fm.beginTransaction()
                .add(R.id.main_frame_layout, chatFragment, "chat")
                .commit();

        setupBottomNav();
        setupDrawer();
    }

    private void setupDrawer() {
        navigationView.setNavigationItemSelectedListener(item -> {
            if (drawerLayout != null) drawerLayout.closeDrawers();
            if (item.getItemId() == R.id.navChat) {
                switchFragment(chatFragment);
            }
            return true;
        });
    }

    private void setupBottomNav() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_profile) switchFragment(profileFragment);
            else if (id == R.id.menu_story) switchFragment(newFeedFragment);
            else if (id == R.id.menu_chat) switchFragment(chatFragment);
            else if (id == R.id.menu_map) switchFragment(mainMapFragment);
            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_chat);
    }

    private void switchFragment(Fragment targetFragment) {
        if (activeFragment != targetFragment) {
            fm.beginTransaction()
                    .hide(activeFragment)
                    .show(targetFragment)
                    .commit();
            activeFragment = targetFragment;
        }
        if (targetFragment instanceof MainMapFragment
                || targetFragment instanceof NewFeedFragment
                || targetFragment instanceof ProfileFragment) {
            appBarLayout.setVisibility(View.GONE);
        } else if (targetFragment instanceof ChatFragment) {
            appBarLayout.setVisibility(View.VISIBLE);
        }
    }
    public void setBottomNavVisible(boolean visible) {
        if (bottomNavigationView != null) {
            // Dùng INVISIBLE thay vì GONE để tránh layout trắng
            bottomNavigationView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        }
    }

    void getChatRequest_Badge() {
        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.menu_chat);
        badgeDrawable.setVerticalOffset(5);
        badgeDrawable.setHorizontalOffset(5);
        badgeDrawable.setBackgroundColor(Color.RED);
    }
}
