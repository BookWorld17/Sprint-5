package com.example.bookapp;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class forgetPasswordActivity extends AppCompatActivity {

    String TAG = "Coursati";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_activity);

        init();
    }
    void init(){
        WebView webView = (WebView) findViewById(R.id.webview);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        JavaScriptReceiver javaScriptReceiver;
        javaScriptReceiver = new JavaScriptReceiver(this);
        webView.addJavascriptInterface(javaScriptReceiver, "JSReceiver");

        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //replaceFragments(newRequestFragment.this);
                return true;
            }

        });

        String url=URLs.FORGET_PASS;
        webView.loadUrl(url);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent lunchscreen = new Intent(forgetPasswordActivity.this, loginActivity.class);
        lunchscreen .setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        lunchscreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(lunchscreen );


    }
}
