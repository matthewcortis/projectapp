package com.example.project.Packages.Chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.Model.ChatRoomModel;
import com.example.project.Packages.Adapter.RecentChatRecyclerAdapter;
import com.example.project.R;
import com.example.project.Utils.FirebaseUtils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class ChatFragment extends Fragment {

    private TextView notification;
    private RecyclerView recyclerView;
    private RecentChatRecyclerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.recyler_view);
        notification = view.findViewById(R.id.notification);
        setupRecyclerView();
        return view;
    }

    private void setupRecyclerView() {
        Query query = FirebaseUtils.allChatroomCollectionReference()
                .whereArrayContains("userIds", FirebaseUtils.currentUserID())
                .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatRoomModel> options =
                new FirestoreRecyclerOptions.Builder<ChatRoomModel>()
                        .setQuery(query, ChatRoomModel.class)
                        .build();

        adapter = new RecentChatRecyclerAdapter(options, getContext(), notification);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(null);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) adapter.stopListening();
    }
}
