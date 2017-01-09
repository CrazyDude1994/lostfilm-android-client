package com.crazydude.lostfilmclient;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.crazydude.common.api.Utils;

/**
 * Created by Crazy on 09.01.2017.
 */

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl("https://lostfilm.tv/");
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        CookieManager.getInstance().removeAllCookie();
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("lostfilm.tv") && Utils.hasSession()) {
                    finish();
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }
}
