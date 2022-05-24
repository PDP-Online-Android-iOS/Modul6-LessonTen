package dev.ogabek.kotlin.manager

import android.content.Context
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask

class StorageManager {

    companion object{

        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val photoRef = storageRef.child("photos")

        fun uploadPhotos(context: Context, uri: Uri, handler: StorageHandler){
            var type: String? = context.contentResolver.getType(uri)
            type = type!!.substring(type.lastIndexOf("/") + 1)
            val fileName = System.currentTimeMillis().toString() + ".${type}"
            val uploadTask: UploadTask = photoRef.child(fileName).putFile(uri)
            uploadTask.addOnSuccessListener { it ->
                val result = it.metadata!!.reference!!.downloadUrl
                result.addOnSuccessListener {
                    val imgUrl = it.toString()
                    handler.onSuccess(imgUrl)
                }.addOnFailureListener { e ->
                    handler.onError(e)
                }
            }.addOnFailureListener{ e ->
                handler.onError(e)
            }
        }
    }
}