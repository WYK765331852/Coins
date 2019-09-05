package com.example.wyk.coins.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.wyk.coins.R;
import com.example.wyk.coins.presenter.CoinHoughCirclePresenter;
import com.example.wyk.coins.presenter.GetPhotoPresenter;
import com.example.wyk.coins.util.FileUtil;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static com.example.wyk.coins.presenter.GetPhotoPresenter.RC_CHOOSE_PHOTO;
import static com.example.wyk.coins.presenter.GetPhotoPresenter.RC_TAKE_PHOTO;

public class CoinProcessingResActivity extends AppCompatActivity {
    ImageView originalIv;
    ImageView resultIv;
    //    ImageView fakeCoin;
    Button setupBt;

    GetPhotoPresenter getPhotoPresenter;
    CoinHoughCirclePresenter houghCirclePresenter;

    int reqCode = 0;

    String filePath;
    String fileName;

    Mat dst;
    Mat img;
    Mat src2FeatureDetector;
    Bitmap bitmap;

    Handler handler;

    private Mat hsv, hue, mask, prob;
    private Rect trackRect;
    private Mat hist;
    private List<Mat> hsvList, hueList;
    private MatOfFloat ranges;

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
            if (reqCode == RC_TAKE_PHOTO) {

            }
        }


    }

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

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initView() {
        originalIv = findViewById(R.id.coin_processing_show_iv);
        resultIv = findViewById(R.id.coin_processing_result_iv);
        setupBt = findViewById(R.id.coin_processing_bt);
//        fakeCoin = findViewById(R.id.coin_processing_fake_coin);

        handler = new HoughProcHandler();


        setupBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (reqCode == RC_CHOOSE_PHOTO) {
                    HoughProcThread thread = new HoughProcThread();
                    thread.start();

                } else if (reqCode == RC_TAKE_PHOTO) {
                    if (bitmap != null) {

//                        dst = houghCirclePresenter.takePhotoHoughCircleProcess(img, dst, bitmap);

                        if (dst != null) {

                            Bitmap dstBm = Bitmap.createBitmap(dst.width(), dst.height(), Bitmap.Config.ARGB_8888);

                            Utils.matToBitmap(dst, dstBm, true);
                            resultIv.setImageBitmap(dstBm);
                        }

                    }
                }
            }
        });

        getPhotoPresenter = new GetPhotoPresenter(CoinProcessingResActivity.this);
        houghCirclePresenter = new CoinHoughCirclePresenter(CoinProcessingResActivity.this);

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

        switch (requestCode) {
            case RC_CHOOSE_PHOTO:
                Uri uri = data.getData();
                String filePath = FileUtil.getFilePathByUri(this, uri);
                fileName = filePath;

                Log.d("aaaa", "filename: " + fileName);
                Log.d("aaaa", "filepath: " + filePath);

                if (!TextUtils.isEmpty(filePath)) {

                    RequestOptions requestOptions = new RequestOptions().skipMemoryCache(true);

                    //显示图片
                    Glide.with(this).load(filePath).apply(requestOptions).into(originalIv);
                }
                break;
            case RC_TAKE_PHOTO:

                try {
                    /*如果拍照成功，将Uri用BitmapFactory的decodeStream方法转为Bitmap*/
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(getPhotoPresenter.getTakePhotoUri()));
                    Log.d("aaaa", "imgUri: " + getPhotoPresenter.getTakePhotoUri());
                    //显示出来
                    originalIv.setImageBitmap(bitmap);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            dst = houghCirclePresenter.takePhotoHoughCircleProcess(img, dst, bitmap);
                        }
                    }).start();


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

//                RequestOptions requestOptions = new RequestOptions().skipMemoryCache(true);
//                File file = new File(Environment.getExternalStorageDirectory() + File.separator + "photoTest" + File.separator);
//                Glide.with(this).load(getPhotoPresenter.getPhotoPath()).apply(requestOptions).into(originalIv);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.coin_menu_back, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.coin_menu_back:
                Intent intent = new Intent(CoinProcessingResActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.coin_feature_detector:
                Intent intent1 = new Intent(CoinProcessingResActivity.this, CoinsFeatureDetectorActivity.class);
                startActivity(intent1);
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class HoughProcThread extends Thread {
        @Override
        public void run() {
            dst = houghCirclePresenter.houghCirclesProcess(img, dst, fileName);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d("aaaa", "dstCoin: " + dst);
            Message message = handler.obtainMessage();
            message.obj = dst;
            handler.sendMessage(message);

        }
    }

    private class HoughProcHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            Bitmap dstBm;
            Mat dst = (Mat) msg.obj;

            if (dst != null) {
                Imgproc.cvtColor(dst, dst, Imgproc.COLOR_BGR2RGBA);

                dstBm = Bitmap.createBitmap(dst.width(), dst.height(), Bitmap.Config.ARGB_8888);

                Utils.matToBitmap(dst, dstBm, true);
                resultIv.setImageBitmap(dstBm);
            }
        }
    }

    public Mat getSrc2FeatureDetector(){
        if (src2FeatureDetector!=null){

        }
        return null;
    }


}
