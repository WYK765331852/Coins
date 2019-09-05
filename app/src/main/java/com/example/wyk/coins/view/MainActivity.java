package com.example.wyk.coins.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wyk.coins.R;
import com.example.wyk.coins.dialog.CoinPicSelectedDialog;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import static com.example.wyk.coins.presenter.GetPhotoPresenter.RC_TAKE_PHOTO;

public class MainActivity extends AppCompatActivity {
    ImageView welcomeIv;
    TextView welcomeTv;
    CoinPicSelectedDialog picSelectedDialog;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                    Mat dst1 = new Mat();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
            Toast.makeText(MainActivity.this, "OpenCV load success!", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coin_main);

        welcomeIv = findViewById(R.id.coin_main_iv);
        welcomeTv = findViewById(R.id.coin_main_tv);
        picSelectedDialog = new CoinPicSelectedDialog(MainActivity.this);

        welcomeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                picSelectedDialog.showDialog();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        picSelectedDialog.dismiss();
    }
}
