package com.example.wyk.coins.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class GetPhotoPresenter implements GetPhotoInterface {

    public static final int RC_CHOOSE_PHOTO = 2;
    public static final int RC_TAKE_PHOTO = 1;

    private Activity activity;
    private Uri imageUri;
    private String photoPath;

    public GetPhotoPresenter(Activity activity) {
        this.activity = activity;

    }


    @Override
    public void choosePhoto() {
        Intent pickPicIntent = new Intent(Intent.ACTION_PICK, null);
        pickPicIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(pickPicIntent, RC_CHOOSE_PHOTO);
    }

    @Override
    public void takePhoto() {
        Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory()+File.separator+"photoTest"+File.separator);
        if (!file.exists()){
            file.mkdirs();
        }

        File photoFile = new File(file, "photo.jpeg");

        try {
            if (photoFile.exists()){
                photoFile.delete();
            }
            photoFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        photoPath = photoFile.getAbsolutePath();
//        imageUri = (Uri) FileProvider.getUriForFile(activity, "",photoFile);
        imageUri = Uri.fromFile(photoFile);
        takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        activity.startActivityForResult(takePicIntent, RC_TAKE_PHOTO);
    }

    @Override
    public Uri getTakePhotoUri() {

        return imageUri;
    }


}
