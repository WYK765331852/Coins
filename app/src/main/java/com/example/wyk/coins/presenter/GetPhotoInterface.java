package com.example.wyk.coins.presenter;

import android.net.Uri;

public interface GetPhotoInterface {

    public void choosePhoto();

    public void takePhoto();

    public Uri getTakePhotoUri();

    public String getPhotoPath();

    public void setPhotoPath(String path);

}
