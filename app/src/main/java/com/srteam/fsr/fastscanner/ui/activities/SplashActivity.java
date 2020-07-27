package com.srteam.fsr.fastscanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.srteam.fsr.fastscanner.R;

public class SplashActivity extends AppCompatActivity {

    private Context mContext;
    private static final int SPLASH_DURATION = 2500;
    private ImageView imvLogo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        }
        mContext = this;
        imvLogo = findViewById(R.id.imvLogo);



        initFunctionality();

    }

    private void initFunctionality() {
        imvLogo.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                SplashActivity.this.finish();
            }
        }, SPLASH_DURATION);

    }

}
