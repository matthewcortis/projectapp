package com.example.project.Packages.Newsfeed;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.project.Packages.Newsfeed.Fragment.AddNewFeedFragment;
import com.example.project.R;


public class NewFeedFragment extends Fragment {
    AppCompatImageView compatImageView;

    public NewFeedFragment() {
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
        View view = inflater.inflate(R.layout.fragment_new_feed, container, false);
        compatImageView = view.findViewById(R.id.AddStoryBtn);


        compatImageView.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_frame_layout, new AddNewFeedFragment())
                    .addToBackStack(null)
                    .commit();
        });
        return view;
    }



    private void getData(){

    }
}