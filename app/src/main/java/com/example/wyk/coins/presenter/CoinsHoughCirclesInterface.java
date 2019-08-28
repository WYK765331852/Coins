package com.example.wyk.coins.presenter;

import android.app.Activity;
import android.graphics.Bitmap;

import org.opencv.core.Mat;

public interface CoinsHoughCirclesInterface {

    public Mat houghCirclesProcess(Mat img, Mat dst, String filename);

    public Mat takePhotoHoughCircleProcess(Mat img, Mat dst, Bitmap bitmap);

//    public Mat getTakePhotoHoughCircleMat();

}
