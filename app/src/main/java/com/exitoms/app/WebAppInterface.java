package com.exitoms.app;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;

import de.mateware.snacky.Snacky;

import static android.content.Context.MODE_PRIVATE;

public class WebAppInterface {
    Context mContext;
    SharedPreferences sharedPreferences;

    // Instantiate the interface and set the context
    WebAppInterface(Context c) {
        mContext = c;
        sharedPreferences = mContext.getSharedPreferences("POLIMA",MODE_PRIVATE);

    }

    // Show a toast from the web page
    @JavascriptInterface
    public void showToast(String toast) {

        Snacky.builder()
                .setActivity((Activity)mContext)
                .setText(toast)
                .setDuration(Snacky.LENGTH_LONG)
                .build()
                .show();

    }



    @JavascriptInterface
    public void OpenNEW(String url, String title) {
        Intent intents = new Intent(mContext, WebActivity.class);
        intents.putExtra("l","f");
        mContext.startActivity(intents);

    }

    @JavascriptInterface
    public void UpdateApp() {

        final String appPackageName = mContext.getPackageName(); // getPackageName() from Context or Activity object
        try {
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }

    }

    @JavascriptInterface
    public void Logout() {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", "");
        editor.putString("mobile", "");
        editor.putString("email", "");
        editor.putString("address", "");
        editor.putString("password", "");
        editor.putString("userId", "");
        editor.commit();
        Intent i = new Intent(mContext, SplashActivity.class);
        mContext.startActivity(i);
        ((Activity) mContext).finish();

    }



    @JavascriptInterface
    public int getAndroidVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }

    @JavascriptInterface
    public void PaymentCloseBtn() {

    }


    @JavascriptInterface
    public String getUserId() {
        return sharedPreferences.getString("userId", "");
    }

    @JavascriptInterface
    public String getUserName() {
        return sharedPreferences.getString("name", "");
    }

    @JavascriptInterface
    public String getUserEmail() {
        return sharedPreferences.getString("email", "");
    }

    @JavascriptInterface
    public String getUserMobile() {
        return sharedPreferences.getString("mobile", "");
    }

    @JavascriptInterface
    public String getUserAddress() {
        return sharedPreferences.getString("address", "");
    }

    @JavascriptInterface
    public void showAndroidVersion(String versionName) {
        Toast.makeText(mContext, versionName, Toast.LENGTH_SHORT).show();
    }
}


