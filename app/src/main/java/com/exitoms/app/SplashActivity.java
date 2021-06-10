package com.exitoms.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    ImageView Logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Logo = findViewById(R.id.logo_image_id);
        Logo.setVisibility(View.GONE);
        sharedPreferences = getSharedPreferences("WHTS", MODE_PRIVATE);

        LogoAnim();
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
        }, 4000);

    }

    public void LogoAnim()
    {
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Logo.setVisibility(View.VISIBLE);
                final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                Logo.startAnimation(animation);
            }
        }, 1000);
    }

}