package com.example.wyk.coins.presenter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.example.wyk.coins.view.CoinProcessingResActivity;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.security.PublicKey;

public class CoinHoughCirclePresenter implements CoinsHoughCirclesInterface {

    Activity activity;
    static Mat src1 = new Mat();

    public CoinHoughCirclePresenter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public Mat houghCirclesProcess(Mat img, Mat dst, String fileName) {
        //初始
        img = Imgcodecs.imread(fileName);
        Log.d("aaaa", "img: " + img);
        //目标
        Mat imgGray = new Mat();
        Mat circles = new Mat();
        dst = img.clone();
        src1 = img.clone();
        Log.d("aaaa", "dst: " + dst);
        Log.d("aaaa", "src1: " + src1);

        if (img.empty()) {
            Toast.makeText(activity, "no picture", Toast.LENGTH_SHORT).show();
        }
        //金字塔均值漂移滤波
        Imgproc.pyrMeanShiftFiltering(dst, img, 15, 80);
        //BGR2GRAY
        Imgproc.cvtColor(img, imgGray, Imgproc.COLOR_BGR2GRAY);
        //高斯滤波
        Imgproc.GaussianBlur(imgGray, imgGray, new Size(3, 3), 0);

//        dst.create(img.size(), img.type());


        //minDist太小，相邻圆检测成一个重合的圆,太大则漏检
        //param2 太小，可检测到更多不存在的圆，越大越接近完美的圆形
        Imgproc.HoughCircles(imgGray, circles, Imgproc.HOUGH_GRADIENT, 1, 60, 100, 32, 70, 130);

        Log.d("aaaa", "circles.cols: " + circles.cols());
        for (int i = 0; i < circles.cols(); i++) {

            float[] info = new float[3];
            circles.get(0, i, info);


            if ((int) info[2] >= 109) {
                Imgproc.circle(dst, new Point((int) info[0], (int) info[1]), (int) info[2], new Scalar(0, 255, 0), 2, 8, 0);
            } else if (((int) info[2] > 104) && ((int) info[2] < 109)) {
                Imgproc.circle(dst, new Point((int) info[0], (int) info[1]), (int) info[2], new Scalar(0, 0, 255), 2, 8, 0);
            } else if (((int) info[2] > 89) && ((int) info[2] < 100)) {
                Imgproc.circle(dst, new Point((int) info[0], (int) info[1]), (int) info[2], new Scalar(255, 0, 0), 2, 8, 0);
            } else if ((int) info[2] < 89) {
                Imgproc.circle(dst, new Point((int) info[0], (int) info[1]), (int) info[2], new Scalar(255, 255, 255), 2, 8, 0);
            }
//                Imgproc.circle(dst, new Point((int) info[0], (int) info[1]), (int) info[2], new Scalar(0, 255, 0), 2, 8, 0);
//            Imgproc.circle(img, new Point((int) info[0], (int) info[1]), (int) info[2], new Scalar(0, 255, 0), 2, 8, 0);
            Imgproc.circle(dst, new Point((int) info[0], (int) info[1]), 4, new Scalar(0, 255, 0), 2, 8, 0);


            Log.d("aaaa", "circles.radius: " + (int) info[2]);

        }
        circles.release();
        imgGray.release();

        Log.d("aaaa", "dstEnd: " + dst);

        return dst;
    }


    @Override
    public Mat takePhotoHoughCircleProcess(Mat img, Mat dst, Bitmap bitmap) {


        img = new Mat();
//        img = new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC3, new Scalar(0));
        Utils.bitmapToMat(bitmap, img);
        Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2RGB);

        img.create(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC3);

        Log.d("aaaa", "img: " + img);

        //目标
        Mat imgGray = new Mat();
        Mat circles = new Mat();
        dst = img.clone();
        if (img.empty()) {
            Toast.makeText(activity, "no picture", Toast.LENGTH_SHORT).show();
        }
        //金字塔均值漂移滤波
        Imgproc.pyrMeanShiftFiltering(dst, img, 15, 80);
        //BGR2GRAY
        Imgproc.cvtColor(img, imgGray, Imgproc.COLOR_BGR2GRAY);
        //高斯滤波
        Imgproc.GaussianBlur(imgGray, imgGray, new Size(3, 3), 0);

        dst.create(img.size(), img.type());
        //minDist太小，相邻圆检测成一个重合的圆,太大则漏检
        //param2 太小，可检测到更多不存在的圆，越大越接近完美的圆形
        Imgproc.HoughCircles(imgGray, circles, Imgproc.HOUGH_GRADIENT, 1, 20, 100, 35, 10, 200);

        Log.d("aaaa", "circles.cols: " + circles.cols());
        for (int i = 0; i < circles.cols(); i++) {

            float[] info = new float[3];
            circles.get(0, i, info);

            Imgproc.circle(dst, new Point((int) info[0], (int) info[1]), (int) info[2], new Scalar(0, 255, 0), 2, 8, 0);
            Imgproc.circle(dst, new Point((int) info[0], (int) info[1]), 2, new Scalar(0, 255, 0), 2, 8, 0);


            Log.d("aaaa", "circles.radius: " + (int) info[2]);

        }
        circles.release();
        imgGray.release();

        return dst;
    }

    @Override
    public Mat getSrc() {
        if (src1!=null){
            return src1;
        }
        Log.d("aaaa", "getsrc1_src1: " + src1);
        return null;
    }


}
