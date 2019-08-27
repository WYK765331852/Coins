package com.example.wyk.coins.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wyk.coins.R;
import com.example.wyk.coins.dialog.CoinPicSelectedDialog;

public class MainActivity extends AppCompatActivity {
    ImageView welcomeIv;
    TextView welcomeTv;
    CoinPicSelectedDialog picSelectedDialog;

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
}
