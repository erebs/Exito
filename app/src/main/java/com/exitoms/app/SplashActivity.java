package com.exitoms.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AnimationUtils;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPreferences = getSharedPreferences("WHTS", MODE_PRIVATE);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                if(sharedPreferences.getString("mid","").length()>5 && sharedPreferences.getString("password","").length()>2)
                {
                    Intent diya = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(diya);
                    finish();

                }else
                {
                    Intent diya = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(diya);
                    finish();

                }
            }
        }, 3000);

    }
}