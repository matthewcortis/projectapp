package com.example.project.Auth;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.project.Auth.Fragment.LoginFragment;
import com.example.project.Auth.Fragment.RegisterFragment;
import com.example.project.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainAuthActivity extends AppCompatActivity {

    TabLayout tab_layout;
    ViewPager2 pager;

    ViewPagerFragmentAdapter adapter;

    public static class ViewPagerFragmentAdapter extends FragmentStateAdapter{
        int size;
        public ViewPagerFragmentAdapter(@NonNull FragmentActivity fragmentActivity, int size) {
            super(fragmentActivity);
            this.size = size;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position){
                case 0:
                    return new LoginFragment();
                case 1:
                    return new RegisterFragment();
            }
            return new LoginFragment();
        }

        @Override
        public int getItemCount() {
            return size;
        }
    }







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_auth);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tab_layout = findViewById(R.id.tab_layout);
        pager = findViewById(R.id.pager);
       adapter = new ViewPagerFragmentAdapter(this, tab_layout.getTabCount());
       pager.setAdapter(adapter);
       pager.setOffscreenPageLimit(2);

       new TabLayoutMediator(tab_layout, pager, ((tab, i) -> {
           if (i == 0){
               tab.setText("Login");

           }else if (i == 1){
               tab.setText("Register");
           }
       })).attach();

    }
}