package com.exitoms.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.icu.text.CaseMap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;

import java.net.URISyntaxException;

import de.mateware.snacky.Snacky;

public class WebActivity extends AppCompatActivity {

    String Title,Url;
    WebView webview,newView;
    TextView PageName;
    KProgressHUD Loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        PageName = findViewById(R.id.PageName);

        try {
            Intent intent = getIntent();
            Title=intent.getStringExtra("t");
            Url=intent.getStringExtra("u");
        }catch (Exception e)
        {
        }
        setMyTitle(Title);
        Loader = KProgressHUD.create(WebActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Loading...")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        webview = (WebView)findViewById(R.id.webview);
        webview.setWebViewClient(new WebClient());
        WebSettings set = webview.getSettings();
        set.setJavaScriptEnabled(true);
        set.setBuiltInZoomControls(false);
        set.supportZoom();
        webview.loadUrl(Url);
        webview.setVerticalScrollBarEnabled(false);
        webview.getSettings().setAppCacheEnabled(true);
        webview.setBackgroundColor(Color.TRANSPARENT);
        webview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webview.getSettings().setSupportMultipleWindows(true);
        webview.addJavascriptInterface(new WebAppInterface(this), "AndroidInterface");
        webview.addJavascriptInterface(new AndroidInterface(this), "JSInterface");
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress >= 100)
                {
                    webview.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            Loader.dismiss();

                        }

                    }, 1000);
                }
                else {
                    webview.setVisibility(View.GONE);
                  Loader.show();

                }
            }
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                webview.removeAllViews();
                newView = new WebView(WebActivity.this);
                newView.setWebViewClient(new WebViewClient());
                newView.setWebChromeClient(new ACWebchromeClient());
                newView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                newView.getSettings().setAllowUniversalAccessFromFileURLs(true);
                WebSettings set = newView.getSettings();
                set.setJavaScriptEnabled(true);
                webview.addView(newView);
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newView);
                resultMsg.sendToTarget();
                //  Toast.makeText(MainActivity.this, resultMsg.toString(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

    }

    public void goBack(View view)
    {
        super.onBackPressed();
    }

    public void setMyTitle(String title)
    {
        PageName.setText(title);
    }

    public class ACWebchromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);

        }}

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        if (webview.canGoBack())
//        { webview.goBack(); }
//        else {super.onBackPressed(); }
    }


    class WebClient extends WebViewClient {

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Log.i("WEB_VIEW_TEST", "error code:" + errorCode);

            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            Loader.dismiss();
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {


            if (url.startsWith("intent://")) {
                try {
                    Context context = view.getContext();
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);

                    if (intent != null) {
                        view.stopLoading();

                        PackageManager packageManager = context.getPackageManager();
                        ResolveInfo info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
                        if (info != null) {
                            context.startActivity(intent);
                        } else {
                            String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                            view.loadUrl(fallbackUrl);



                        }

                        return true;
                    }
                } catch (URISyntaxException e) {

                    Log.e("INETNTTAG", "Can't resolve intent://", e);
                }
            }
            else if(url.startsWith("market://"))
            {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(i);
            }
            else if(url.startsWith("tel:"))
            {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(i);
            }
            else
            {
                view.loadUrl(url);

            }



            return true;
        } }

    public class AndroidInterface{

        public Context mContext;
        public AndroidInterface(Context c) {

            mContext = c;
        }



        @JavascriptInterface
        public void showToast(String toast) {

            Snacky.builder()
                    .setActivity(WebActivity.this)
//                .setActionText("ACTION")
                    .setActionClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //do something
                        }
                    })
                    .setText(toast)
                    .setDuration(Snacky.LENGTH_LONG)
                    .build()
                    .show();
        }


    }

    public void PrintoPdf(View view)
    {
        createWebPagePrint(webview);
    }

    public  void createWebPagePrint(WebView webView) {
		/*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return;*/
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter();
        String jobName = getString(R.string.app_name) + " Document";
        PrintAttributes.Builder builder = new PrintAttributes.Builder();
        builder.setMediaSize(PrintAttributes.MediaSize.ISO_A5);
        PrintJob printJob = printManager.print(jobName, printAdapter, builder.build());

        if(printJob.isCompleted()){
            Toast.makeText(getApplicationContext(), "Print job Completed.", Toast.LENGTH_LONG).show();
        }
        else if(printJob.isFailed()){
            Toast.makeText(getApplicationContext(), "Print job Failed", Toast.LENGTH_LONG).show();
        }
        // Save the job object for later status checking
    }

}