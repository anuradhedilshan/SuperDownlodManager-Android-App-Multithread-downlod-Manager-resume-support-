package lk.lab24.sdm;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Objects;

import lk.lab24.sdm.dialogs.newDownlod;

public class DownlodWebView extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downlod_web_view);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBar.setSubtitle("inter GRted Web Browser");
        Intent intent = getIntent();
        String url = intent.getStringExtra("URL");
        Log.d("fuck", "WEB VIEW URL :" + url);
        ProgressBar progressBar = findViewById(R.id.webViewProgress);
        webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webView.loadUrl(url);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100) {
                    progressBar.setProgress(progress);
                    Log.d("fuck", "progress:" + progress);
                }
                if (progress == 100) {
                    actionBar.setSubtitle(view.getTitle());
                    Toast.makeText(getApplicationContext(), "WEB LODED", Toast.LENGTH_SHORT).show();
                }
            }

        });
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                actionBar.setSubtitle(webView.getTitle());
                return true;
            }
        });
        webView.setDownloadListener((url1, userAgent, contentDisposition, mimetype, contentLength) -> {
            Log.d("fuck", "From WebVIEw : " + url1);
            Intent i = new Intent();
            i.putExtra("URL", url1);
            setResult(50000, i);
            finish();

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.webview, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.refresh:
                webView.reload();
                break;
            case R.id.forword:
                if (webView.canGoForward()) {
                    webView.goForward();
                }


                break;
            case R.id.backword:
                if (webView.canGoBack()) {
                    webView.goBack();
                }
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
