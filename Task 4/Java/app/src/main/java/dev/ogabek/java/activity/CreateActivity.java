package dev.ogabek.java.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter;

import java.util.ArrayList;

import dev.ogabek.java.R;
import dev.ogabek.java.manager.DatabaseHandler;
import dev.ogabek.java.manager.DatabaseManager;
import dev.ogabek.java.manager.StorageHandler;
import dev.ogabek.java.manager.StorageManager;
import dev.ogabek.java.model.Post;

public class CreateActivity extends BaseActivity {

    ImageView iv_photo;
    Uri pickedPhoto;
    ArrayList<Uri> allPhotos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        initViews();

    }

    private void initViews() {
        iv_photo = findViewById(R.id.iv_photo);
        ImageView iv_close = findViewById(R.id.iv_close);
        EditText et_title = findViewById(R.id.et_title);
        EditText et_body = findViewById(R.id.et_body);
        Button b_create = findViewById(R.id.b_create);

        ImageView iv_camera = findViewById(R.id.iv_camera);

        iv_camera.setOnClickListener(view -> pickUserPhoto());

        iv_close.setOnClickListener(view -> finish());
        b_create.setOnClickListener(view -> {
            String title = et_title.getText().toString().trim();
            String body = et_body.getText().toString().trim();
            Post post = new Post(title, body);
            storePost(post);
        });
    }

    private void storePost(Post post) {
        Log.d("Post", "storePost: $pickedPhoto");
        if (pickedPhoto != null) {
            storeDatabase(post);
        } else {
            storeStorage(post);
        }
    }

    private void storeDatabase(Post post) {
        showLoading(this);
        StorageManager.uploadPhotos(this, pickedPhoto, new StorageHandler() {
            @Override
            public void onSuccess(String imgUrl) {
                post.setImg(imgUrl);
                Log.d("Post", "onSuccess: " + imgUrl);
                storeStorage(post);
            }

            @Override
            public void onError(Exception exception) {
                Log.d("Post", "onError: " + exception.getMessage());
                storeDatabase(post);
            }
        });
    }

    private void pickUserPhoto() {
        FishBun.with(this)
                .setImageAdapter(new GlideAdapter())
                .setMaxCount(1)
                .setMinCount(1)
                .setSelectedImages(allPhotos)
                .startAlbumWithActivityResultCallback(photoLauncher);
    }

    private void storeStorage(Post post) {
        DatabaseManager.storePost(post, new DatabaseHandler() {
            @Override
            public void onSuccess(ArrayList<Post> posts) {
                Log.d("Post", "Uploaded");
                dismissLoading();
                finishIntent();
            }

            @Override
            public void onSuccess() {
                Log.d("Post", "Uploaded");
                dismissLoading();
                finishIntent();
            }

            @Override
            public void onError() {
                dismissLoading();
                Log.d("Post", "onError: Failed");
            }
        });
    }

    void finishIntent() {
        Intent returnIntent = getIntent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private ActivityResultLauncher photoLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            if (result.getData().getParcelableArrayListExtra(FishBun.INTENT_PATH) != null) {
                allPhotos = result.getData().getParcelableArrayListExtra(FishBun.INTENT_PATH);
            } else {
                allPhotos = new ArrayList<>();
            }
            pickedPhoto = allPhotos.get(0);
            iv_photo.setImageURI(pickedPhoto);
        }
    });

}