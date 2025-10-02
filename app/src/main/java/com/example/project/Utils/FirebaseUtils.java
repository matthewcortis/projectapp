package com.example.project.Utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class FirebaseUtils {
    public static  String currentUserID(){
        return FirebaseAuth.getInstance().getUid();
    }

    public static boolean isLoggedIn(){
        return currentUserID() != null;
    }
    public static FirebaseFirestore db (){
        return FirebaseFirestore.getInstance();
    }
    public static StorageReference putFile(){
        return FirebaseStorage.getInstance().getReference();
    }
    public static DocumentReference currentUserDetails(){

        return FirebaseFirestore.getInstance().collection("users").document(currentUserID());
    }
    public static DocumentReference status(String id){
        return FirebaseFirestore.getInstance().collection("users").document(id);
    }
    public static StorageReference  getCurrentProfilePicStorageRef(){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(FirebaseUtils.currentUserID());
    }
    public static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("users");
    }
    public static StorageReference getOtherProfilePicStorageRef(String otherUserId){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(otherUserId);
    }
    public static StorageReference putMediaImagesStory(){
        return putFile().child("MediaStory").child("Images").child(currentUserID()+"_"+ UUID.randomUUID().toString());
    }
    public static StorageReference putMediaVideosStory(){
        return putFile().child("MediaStory").child("Videos").child(currentUserID()+"_"+UUID.randomUUID().toString());
    }
    public static CollectionReference postStory(){
        return FirebaseFirestore.getInstance().collection("post");
    }
    public static DocumentReference deletePostStory(String documentID){
        return postStory().document(documentID);
    }

}
