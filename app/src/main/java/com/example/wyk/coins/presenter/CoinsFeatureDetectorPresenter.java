package com.example.wyk.coins.presenter;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.AKAZE;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;

public class CoinsFeatureDetectorPresenter implements CoinsFeatureDetectorInterface {

    @Override
    public void featureDescriptor(ImageView image, Mat src, Mat dst) {
        //imageview到mat
        Mat boxImage = new Mat();
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();

        boxImage.create(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC3);

        Utils.bitmapToMat(bitmap, boxImage);
//        Imgproc.cvtColor(boxImage, boxImage, Imgproc.COLOR_BGR2RGB);

//        FeatureDetector detector = FeatureDetector.create(FeatureDetector.AKAZE);
//        DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.AKAZE);

        AKAZE akaze = AKAZE.create();

        // 关键点检测
        MatOfKeyPoint keyPoints_box = new MatOfKeyPoint();
        MatOfKeyPoint keyPoints_scene = new MatOfKeyPoint();
//        detector.detect(boxImage, keyPoints_box);
//        detector.detect(src, keyPoints_scene);
        akaze.detect(boxImage, keyPoints_box);
        akaze.detect(src, keyPoints_scene);

        // 描述子生成
        Mat descriptor_box = new Mat();
        Mat descriptor_scene = new Mat();
//        descriptorExtractor.compute(boxImage, keyPoints_box, descriptor_box);
//        descriptorExtractor.compute(src, keyPoints_scene, descriptor_scene);
        akaze.compute(boxImage, keyPoints_box, descriptor_box);
        akaze.compute(src, keyPoints_scene, descriptor_scene);

        // 特征匹配
        MatOfDMatch matches = new MatOfDMatch();
        DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
        descriptorMatcher.match(descriptor_box, descriptor_scene, matches);
        Features2d.drawMatches(boxImage, keyPoints_box, src, keyPoints_scene, matches, dst);

        // 释放内存
        keyPoints_box.release();
        keyPoints_scene.release();
        descriptor_box.release();
        descriptor_scene.release();
        matches.release();

    }
}
