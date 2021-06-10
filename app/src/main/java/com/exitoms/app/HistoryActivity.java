package com.exitoms.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);


    }

    public void DashboardBtn(View view)
    {
        Intent intents = new Intent(HistoryActivity.this, MainActivity.class);
        startActivity(intents);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        finish();
    }

    public void AccountBtn(View view)
    {
        Intent intents = new Intent(HistoryActivity.this, AccountActivity.class);
        startActivity(intents);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        finish();
    }

    public void MoreBtn(View view)
    {
        Intent intents = new Intent(HistoryActivity.this, MoreActivity.class);
        startActivity(intents);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        finish();
    }

}