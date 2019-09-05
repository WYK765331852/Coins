package com.example.wyk.coins.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wyk.coins.R;
import com.example.wyk.coins.presenter.CoinHoughCirclePresenter;
import com.example.wyk.coins.presenter.CoinsFeatureDetectorPresenter;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class CoinsFeatureDetectorActivity extends AppCompatActivity {
    ImageView originalIv;
    ImageView srcIv;
    ImageView dstIv;

    Mat src;
    Mat dst;

    CoinHoughCirclePresenter coinHoughCirclePresenter;
    CoinsFeatureDetectorPresenter coinsFeatureDetectorPresenter;

    Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coin_feature_detector);

        initView();
    }

    private void initView() {
        originalIv = findViewById(R.id.coin_feature_detector_original_iv);
        srcIv = findViewById(R.id.coin_feature_detector_src_iv);
        dstIv = findViewById(R.id.coin_feature_detector_dst_iv);

        coinHoughCirclePresenter = new CoinHoughCirclePresenter(CoinsFeatureDetectorActivity.this);
        coinsFeatureDetectorPresenter = new CoinsFeatureDetectorPresenter();

        src = new Mat();
        dst = new Mat();

        handler = new FeatureDetectorHandler();

        if (coinHoughCirclePresenter.getSrc() != null) {

            src = coinHoughCirclePresenter.getSrc().clone();

            Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2RGBA);

            Bitmap srcBitmap = Bitmap.createBitmap(src.width(), src.height(), Bitmap.Config.ARGB_8888);

            Utils.matToBitmap(src, srcBitmap, true);

            srcIv.setImageBitmap(srcBitmap);

            FeatureDetectorThread thread = new FeatureDetectorThread();
            thread.start();
        } else {
            Toast.makeText(CoinsFeatureDetectorActivity.this, "houghProcDst = null", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.coin_feature_menu_back, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.coin_feature_menu_back:
                Intent intent = new Intent(CoinsFeatureDetectorActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class FeatureDetectorHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            Bitmap dstBm;
            Mat dst = (Mat) msg.obj;

            if (dst != null) {

//                Imgproc.cvtColor(dst, dst, Imgproc.COLOR_BGR2RGBA);

                dstBm = Bitmap.createBitmap(dst.width(), dst.height(), Bitmap.Config.ARGB_8888);

                Utils.matToBitmap(dst, dstBm, true);
                dstIv.setImageBitmap(dstBm);
            }
        }
    }

    class FeatureDetectorThread extends Thread {
        @Override
        public void run() {
            super.run();

            coinsFeatureDetectorPresenter.featureDescriptor(originalIv, src, dst);

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Message message = handler.obtainMessage();
            message.obj = dst;
            handler.sendMessage(message);

        }
    }

}
