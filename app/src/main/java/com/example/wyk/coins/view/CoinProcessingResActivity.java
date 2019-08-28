package com.example.wyk.coins.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.wyk.coins.R;
import com.example.wyk.coins.presenter.GetPhotoPresenter;
import com.example.wyk.coins.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;

import static com.example.wyk.coins.presenter.GetPhotoPresenter.RC_CHOOSE_PHOTO;
import static com.example.wyk.coins.presenter.GetPhotoPresenter.RC_TAKE_PHOTO;

public class CoinProcessingResActivity extends AppCompatActivity {
    ImageView originalIv;
    ImageView resultIv;
    Button setupBt;

    GetPhotoPresenter getPhotoPresenter;

    int reqCode = 0;

    String filePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coin_pic_processing);

        initView();

        Intent intent = getIntent();
        reqCode = intent.getIntExtra("getPic", 0);

        requestPermissionResult();

        Log.d("aaaa", "reqCode: " + reqCode);

    }

    private void initView() {
        originalIv = findViewById(R.id.coin_processing_show_iv);
        resultIv = findViewById(R.id.coin_processing_result_iv);
        setupBt = findViewById(R.id.coin_processing_bt);

        getPhotoPresenter = new GetPhotoPresenter(CoinProcessingResActivity.this);

        filePath = Environment.getExternalStorageDirectory().getPath();
        filePath = filePath + "/" + "temp.jpeg";
        getPhotoPresenter.setPhotoPath(filePath);
    }

    private void requestPermissionResult() {

        switch (reqCode) {
            case RC_TAKE_PHOTO:
                getPhotoPresenter.takePhoto();
                break;
            case RC_CHOOSE_PHOTO:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RC_CHOOSE_PHOTO);
                } else {
                    //已经授权
                    getPhotoPresenter.choosePhoto();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        requestCode = reqCode;

        Log.d("aaaa", "requestCode: " + requestCode);

        switch (requestCode) {
            case RC_CHOOSE_PHOTO:
                Uri uri = data.getData();
                String filePath = FileUtil.getFilePathByUri(this, uri);

                if (!TextUtils.isEmpty(filePath)) {

                    RequestOptions requestOptions = new RequestOptions().skipMemoryCache(true);

                    //显示图片
                    Glide.with(this).load(filePath).apply(requestOptions).into(originalIv);

                }
                break;
            case RC_TAKE_PHOTO:

                try {
                    /*如果拍照成功，将Uri用BitmapFactory的decodeStream方法转为Bitmap*/
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(getPhotoPresenter.getTakePhotoUri()));
                    //显示出来
                    originalIv.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


//                RequestOptions requestOptions = new RequestOptions().skipMemoryCache(true);

//                File file = new File(Environment.getExternalStorageDirectory() + File.separator + "photoTest" + File.separator);


//                Glide.with(this).load(getPhotoPresenter.getPhotoPath()).apply(requestOptions).into(originalIv);

        }
    }
}
