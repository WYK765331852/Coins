package com.example.wyk.coins.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wyk.coins.R;
import com.example.wyk.coins.view.CoinCameraViewProcessActivity;
import com.example.wyk.coins.view.CoinProcessingResActivity;
import com.example.wyk.coins.view.OpencvCameraActivity;

import static com.example.wyk.coins.presenter.GetPhotoPresenter.RC_CHOOSE_PHOTO;
import static com.example.wyk.coins.presenter.GetPhotoPresenter.RC_TAKE_PHOTO;

public class CoinPicSelectedDialog extends Dialog {

    private Context context;
    private Activity activity;
    private TextView albumTv;
    private TextView cameraTv;

    private selectFromAlbumOnClickListener albumOnClickListener;
    private selectFromCameraOnClickListener cameraOnClickListener;

    public CoinPicSelectedDialog(Context context) {
        super(context);
        this.context = context;
        this.activity = (Activity) context;
    }


//    private View initView() {
//        View view = LayoutInflater.from(context).inflate(R.layout.coin_pic_dialog, null);
//        setContentView(view);
//
//        albumTv = view.findViewById(R.id.coin_dialog_album);
//        cameraTv = view.findViewById(R.id.coin_dialog_camera);
//
//        albumOnClickListener = new selectFromAlbumOnClickListener();
//        cameraOnClickListener = new selectFromCameraOnClickListener();
//
//        albumTv.setOnClickListener(albumOnClickListener);
//        cameraTv.setOnClickListener(cameraOnClickListener);
//
//        return view;
//    }

    private class selectFromAlbumOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Toast.makeText(context, "Select from album", Toast.LENGTH_SHORT).show();
            Intent getFromAlbumIntent = new Intent(context, CoinProcessingResActivity.class);
            getFromAlbumIntent.putExtra("getPic", RC_CHOOSE_PHOTO);
            context.startActivity(getFromAlbumIntent);
            activity.finish();
        }
    }
    private class selectFromCameraOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            Toast.makeText(context, "Select through camera", Toast.LENGTH_SHORT).show();
            Intent getFromCameraIntent = new Intent(context, OpencvCameraActivity.class);
            getFromCameraIntent.putExtra("getPic", RC_TAKE_PHOTO);
            context.startActivity(getFromCameraIntent);
            activity.finish();
        }
    }

    public void showDialog(){

        View view = LayoutInflater.from(context).inflate(R.layout.coin_pic_dialog, null);

        albumTv = view.findViewById(R.id.coin_dialog_album);
        cameraTv = view.findViewById(R.id.coin_dialog_camera);

        albumOnClickListener = new selectFromAlbumOnClickListener();
        cameraOnClickListener = new selectFromCameraOnClickListener();

        albumTv.setOnClickListener(albumOnClickListener);
        cameraTv.setOnClickListener(cameraOnClickListener);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("选择照片来源")
                .setView(view)
                .setIcon(R.drawable.coin_lucky_coin)
                .show();

    }


}
