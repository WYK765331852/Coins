package com.example.wyk.coins.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wyk.coins.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import static com.example.wyk.coins.presenter.GetPhotoPresenter.RC_TAKE_PHOTO;

public class CoinCameraViewProcessActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    int reqCode = 0;
    int count = 0;
    Mat dst;
    Mat src;
    Mat gray;

    JavaCameraView cameraView;
    Button proBt;


//    static {
//        System.loadLibrary("native-lib");
//    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                    dst = new Mat();
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
            if (reqCode == RC_TAKE_PHOTO) {
                cameraView.enableView();
                //设置为可见
                cameraView.setVisibility(SurfaceView.VISIBLE);
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coin_camera_view);

        Intent intent = getIntent();
        reqCode = intent.getIntExtra("getPic", 0);

        cameraView = findViewById(R.id.coin_camera_view);
        proBt = findViewById(R.id.coin_camera_view_bt);

        cameraView.setCvCameraViewListener(this);

        proBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count < 1) {
                    count++;
                } else {
                    count = 0;
                }
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraView != null) {
            cameraView.disableView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraView != null) {
            cameraView.disableView();
        }
    }

    //对每一帧数据进行操作
    public void nativeRgba(int count, Mat img) {
        switch (count){
            case 1:

                break;
            case 0:
                break;
                default:
                    break;
        }

    }


    /**
     * 当摄像机预览开始时，这个方法就会被调用。在调用该方法之后，框架将通过onCameraFrame()回调向客户端发送。
     *
     * @param width  - 帧的宽度
     * @param height - 帧的高度
     */
    @Override
    public void onCameraViewStarted(int width, int height) {

        src = new Mat(width, height, CvType.CV_8UC3);

    }

    /**
     * 当摄像机预览由于某种原因被停止时，这个方法就会被调用。
     * 在调用这个方法之后，不会通过onCameraFrame()回调来传递任何帧。
     */
    @Override
    public void onCameraViewStopped() {
        src.release();
    }

    /**
     * 当需要完成框架的交付时，将调用此方法。
     * 返回值-是一个修改后的帧，需要在屏幕上显示。
     * TODO: pass the parameters specifying the format of the frame (BPP, YUV or RGB and etc)
     *
     * @param inputFrame
     */
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        /**
         * This method returns RGBA Mat with frame
         */
        src = inputFrame.rgba();
        src.create(src.size(), CvType.CV_8UC3);
        gray = inputFrame.gray();
        gray.create(gray.size(), CvType.CV_8UC3);
//        long addr = src.getNativeObjAddr();
//        nativeRgba(addr);
        switch (count){
            case 1:
                //金字塔均值漂移滤波
//                Imgproc.pyrMeanShiftFiltering(src, src, 15, 80);
                //BGR2GRAY
                Imgproc.cvtColor(gray, src, Imgproc.COLOR_BGR2GRAY);
                //高斯滤波
//                Imgproc.GaussianBlur(gray, gray, new Size(3, 3), 0);

                break;
            case 0:
                src = inputFrame.rgba();
                break;
            default:
                src = inputFrame.rgba();
                break;
        }

        return src;
    }

    //Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.coin_menu_back, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.coin_menu_back:
                Intent intent = new Intent(CoinCameraViewProcessActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
