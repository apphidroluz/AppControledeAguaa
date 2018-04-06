package br.com.hidroluz.HidroluzWaterControl;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class Noticias extends Fragment {
    public static ProgressDialog progressDialog;
    private WebView mWebView;
    ProgressBar progressBar;
    View rootview;

    public class myWebClient extends WebViewClient {
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Noticias.this.progressBar.setVisibility(0);
            view.loadUrl(url);
            return true;
        }

        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Noticias.this.progressBar.setVisibility(8);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootview = inflater.inflate(R.layout.activity_noticias, container, false);
        return this.rootview;
    }

    public void onStart() {
        super.onStart();
        not();
    }

    public void not() {
        this.mWebView = (WebView) getView().findViewById(R.id.webView1);
        this.progressBar = (ProgressBar) getView().findViewById(R.id.progressBar1);
        WebSettings ws = this.mWebView.getSettings();
        ws.setJavaScriptEnabled(true);
        this.mWebView.setWebViewClient(new myWebClient());
        ws.setSupportZoom(false);
        this.mWebView.loadUrl("http://hidroluz.com.br/");
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4 || !this.mWebView.canGoBack()) {
            return false;
        }
        this.mWebView.goBack();
        return true;
    }
}
