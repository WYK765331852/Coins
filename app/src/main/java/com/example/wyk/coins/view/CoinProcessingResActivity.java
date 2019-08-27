package com.example.wyk.coins.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.wyk.coins.R;
import com.example.wyk.coins.presenter.GetPhotoPresenter;
import com.example.wyk.coins.util.FileUtil;

import static com.example.wyk.coins.presenter.GetPhotoPresenter.RC_CHOOSE_PHOTO;
import static com.example.wyk.coins.presenter.GetPhotoPresenter.RC_TAKE_PHOTO;

public class CoinProcessingResActivity extends AppCompatActivity {
    ImageView originalIv;
    ImageView resultIv;
    Button setupBt;

    GetPhotoPresenter getPhotoPresenter;

    int reqCode = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coin_pic_processing);

        originalIv = findViewById(R.id.coin_processing_show_iv);
        resultIv = findViewById(R.id.coin_processing_result_iv);
        setupBt = findViewById(R.id.coin_processing_bt);

        getPhotoPresenter = new GetPhotoPresenter(CoinProcessingResActivity.this);

        Intent intent = getIntent();
        reqCode = intent.getIntExtra("getPic", 0);


        //检查是否授权
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RC_CHOOSE_PHOTO);
        } else {
            //已经授权
            getPhotoPresenter.choosePhoto();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        requestCode = reqCode;

        switch (requestCode) {
            case RC_TAKE_PHOTO:
                if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    getPhotoPresenter.takePhoto();
                }
                break;
            case RC_CHOOSE_PHOTO:
                getPhotoPresenter.choosePhoto();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        requestCode = reqCode;
        switch (requestCode) {
            case RC_CHOOSE_PHOTO:
                Uri uri = data.getData();
                String filePath = FileUtil.getFilePathByUri(this, uri);


        }
    }
}
