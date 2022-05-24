package dev.ogabek.java.manager;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class StorageManager {

    static FirebaseStorage storage = FirebaseStorage.getInstance();
    static StorageReference storageRef = storage.getReference();
    static StorageReference photoRef = storageRef.child("photos");

    public static void uploadPhotos(Context context, Uri uri, StorageHandler handler){
        String type = context.getContentResolver().getType(uri);
        type = type.substring(type.lastIndexOf("/") + 1);
        String fileName = System.currentTimeMillis() + "." + type;
        UploadTask uploadTask = photoRef.child(fileName).putFile(uri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
            result.addOnSuccessListener( it -> {
                String imgUrl = it.toString();
                handler.onSuccess(imgUrl);
            }).addOnFailureListener(handler::onError);
        }).addOnFailureListener(handler::onError);
    }

}
