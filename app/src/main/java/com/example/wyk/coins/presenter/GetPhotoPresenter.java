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
import java.text.SimpleDateFormat;
import java.util.Date;

public class GetPhotoPresenter implements GetPhotoInterface {

    public static final int RC_CHOOSE_PHOTO = 2;
    public static final int RC_TAKE_PHOTO = 1;
    private static final String FILE_PROVIDER_AUTHORITY = "com.example.wyk.fileprovider";

    private Activity activity;
    private Uri imageUri;
    private String photoPath;
    private String filePath;

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

        File imageFile = createImageFile();
        if (imageFile != null) {
            /*7.0以上要通过FileProvider将File转化为Uri*/
            imageUri = FileProvider.getUriForFile(activity, FILE_PROVIDER_AUTHORITY, imageFile);

        }
        //将用于输出的文件Uri传递给相机
        takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        activity.startActivityForResult(takePicIntent, RC_TAKE_PHOTO);


//        File file = new File(Environment.getExternalStorageDirectory()+File.separator+"photoTest"+File.separator);
//        if (!file.exists()){
//            file.mkdirs();
//        }

//        File photoFile = new File(file, "photo.jpeg");
//
//        try {
//            if (photoFile.exists()){
//                photoFile.delete();
//            }
//            photoFile.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        photoPath = photoFile.getAbsolutePath();
//        imageUri = (Uri) FileProvider.getUriForFile(activity, FILE_PROVIDER_AUTHORITY,photoFile);
////        imageUri = Uri.fromFile(photoFile);
//        takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//        activity.startActivityForResult(takePicIntent, RC_TAKE_PHOTO);

//        File file1 = new File(filePath);
////        imageUri = Uri.fromFile(file1);
//        imageUri = FileProvider.getUriForFile(activity, FILE_PROVIDER_AUTHORITY, file1);
//        photoPath = file1.getAbsolutePath();
//
//        takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//        activity.startActivityForResult(takePicIntent, RC_TAKE_PHOTO);


    }

    @Override
    public Uri getTakePhotoUri() {
        return imageUri;
    }

    @Override
    public String getPhotoPath() {
        return photoPath;
    }

    @Override
    public void setPhotoPath(String path) {

        this.filePath = path;
    }

    //创建用于存储照片的文件
    private File createImageFile() {
        //以时间命名，避免重名
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = null;
        try {
            imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageFile;
    }


}
