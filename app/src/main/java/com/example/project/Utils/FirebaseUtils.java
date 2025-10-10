package com.example.project.Utils;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
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

    public static Task<QuerySnapshot> getAllUsers() {
        return allUserCollectionReference().get();
    }
    public static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("users");
    }
    public static Task<DocumentSnapshot> otherUserDetails(String userId) {
        return FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .get();
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
    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }

    public static String getChatroomId(String userId1,String userId2){
        if(userId1.hashCode()<userId2.hashCode()){
            return userId1+"_"+userId2;
        }else{
            return userId2+"_"+userId1;
        }
    }
    public static CollectionReference allChatroomCollectionReference(){
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    public static DocumentReference getOtherUserFromChatroom(List<String> userIds){
        if(userIds.get(0).equals(FirebaseUtils.currentUserID())){
            return allUserCollectionReference().document(userIds.get(1));
        }else{
            return allUserCollectionReference().document(userIds.get(0));
        }
    }
    public static CollectionReference getChatroomMessageReference(String chatroomId){
        return getChatroomReference(chatroomId).collection("chats");
    }

    public static DocumentReference getChatroomReference(String chatroomId){
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }
    public static StorageReference putImageChat(){
        return putFile().child("images").child("roomImages").child(currentUserID()+"_"+UUID.randomUUID().toString());
    }
    public  static DocumentReference updateLastRead(String documentId){
        return  FirebaseFirestore.getInstance().collection("chatrooms").document(documentId);

    }
}
