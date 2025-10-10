package com.example.project.Packages.Newsfeed;

import android.location.Location;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import com.example.project.Utils.Constant;
import com.example.project.Utils.GoogleMapsUtils;
import com.google.firebase.firestore.Query;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.project.Model.NewFeedModel;
import com.example.project.Packages.Adapter.NewFeedRecyclerAdapter;
import com.example.project.Packages.Home.HomeMainActivity;
import com.example.project.Packages.Newsfeed.Fragment.AddNewFeedFragment;
import com.example.project.R;
import com.example.project.Utils.AndroidUtils;
import com.example.project.Utils.FirebaseUtils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;
import java.util.List;


public class NewFeedFragment extends Fragment {
    AppCompatImageView compatImageView;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    NewFeedRecyclerAdapter adapter;
    ImageView imageView;
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
        recyclerView = view.findViewById(R.id.recyclerViewStory);
        swipeRefreshLayout = view.findViewById(R.id.container);
        imageView = view.findViewById(R.id.profile_image_view);
        compatImageView.setOnClickListener(v -> {
            ((HomeMainActivity) requireActivity()).setBottomNavVisible(false);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_frame_layout, new AddNewFeedFragment())
                    .addToBackStack(null)
                    .commit();
        });
        AndroidUtils.loadCurrentProfilePicture(requireContext(), imageView);
        refreshData();
        return view;
    }


    private void refreshData() {
        swipeRefreshLayout.setOnRefreshListener(this::refreshData);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.my_primary));
        swipeRefreshLayout.setRefreshing(true);

        GoogleMapsUtils.getCurrentLocation(requireContext(), requireActivity(), new GoogleMapsUtils.LocationCallback() {
            @Override
            public void onLocationReceived(Location location) {
                double myLat = location.getLatitude();
                double myLon = location.getLongitude();
                setUpRecyclerView(myLat, myLon);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onLocationError(String error) {
                AndroidUtils.showToast(requireContext(), "Không lấy được vị trí: " + error);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void setUpRecyclerView(double myLat, double myLon) {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new NewFeedRecyclerAdapter(requireContext());
        recyclerView.setAdapter(adapter);
        FirebaseUtils.postStory()
                .orderBy(Constant.FIELD_POST_TIMESTAMP, Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<NewFeedModel> allPosts = new ArrayList<>();
                    for (var doc : snapshot.getDocuments()) {
                        NewFeedModel post = doc.toObject(NewFeedModel.class);
                        if (post != null) allPosts.add(post);
                    }

                    List<NewFeedModel> nearbyPosts = GoogleMapsUtils.filterNearbyPosts(allPosts, myLat, myLon, 5);
                    adapter.setPosts(nearbyPosts);

                    AndroidUtils.showToast(requireContext(),
                            "Hiển thị " + nearbyPosts.size() + " bài viết trong bán kính 5 km");
                })
                .addOnFailureListener(e ->
                        AndroidUtils.showToast(requireContext(), "Lỗi tải bài viết: " + e.getMessage()));
    }


    @Override
    public void onStart() {
        super.onStart();
    }



}