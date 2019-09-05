package com.example.wyk.coins.presenter;

import android.widget.ImageView;

import org.opencv.core.Mat;

public interface CoinsFeatureDetectorInterface {
    public void featureDescriptor(ImageView image, Mat src, Mat dst);
}
