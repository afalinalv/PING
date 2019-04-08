package site.app4web.ping;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;


import org.apache.commons.validator.routines.UrlValidator;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.editText);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        if (isConnected())
            Toast.makeText(getApplicationContext(), "Инет Есть", Toast.LENGTH_SHORT).show();
        else Toast.makeText(getApplicationContext(), "НЕТ Инет ", Toast.LENGTH_SHORT).show();
        webView = findViewById(R.id.webView);
        // включаем поддержку JavaScript
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());
        // указываем страницу загрузки
        //webView.loadUrl("http://developer.alexanderklimov.ru/android");
    }
    // https://xakep.ru/2015/06/11/coding-android-widget-site-availability/
    public void onClick(View view) {
        // прячем клавиатуру. butCalculate - это кнопка
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        String TryText = editText.getText().toString();
            if (tryHttp(TryText)) { // работает изучать
                TryText = editText.getText().toString();
                Toast.makeText(getApplicationContext(), "Сайт доступен", Toast.LENGTH_SHORT).show();
                webView.loadUrl(TryText);
            } else {
                Toast.makeText(getApplicationContext(), "Сайт НЕ доступен", Toast.LENGTH_SHORT).show();
            }
    }

    public boolean isConnected() {
        NetworkInfo ni = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }

    public  boolean tryHttp(String url) {
        String url_temp;
        if (!url.contains(".")) return false;
        url = url.trim();
        if (url.startsWith(".")) return false;
        if (url.endsWith(".")) return false;
        if (url.contains(" ")) return false;
        url = url.toLowerCase();
        url_temp = url.replace("www.","");
        if (pingHttp(url_temp)) return true;
        if (url.startsWith("www.")) url_temp = "http://"+ url;
        if (pingHttp(url_temp)) return true;
        if (url.startsWith("www.")) url_temp = "https://"+ url;
        if (pingHttp(url_temp)) return true;
        if (url.startsWith("www.")) url_temp = url.replace("www.","http://");
        if (pingHttp(url_temp)) return true;
        if (url.startsWith("www.")) url_temp = url.replace("www.","https://");
        if (pingHttp(url_temp)) return true;
        if (!url.startsWith("http")) url_temp = "http://"+ url;
        if (pingHttp(url_temp)) return true;
        if (!url.startsWith("http")) url_temp = "https://"+ url;
        if (pingHttp(url_temp)) return true;
        String[] schemes = {"http","https"};
               // UrlValidator urlValidator = new UrlValidator(schemes);
        if (!new UrlValidator(schemes).isValid(url)) {
            Toast.makeText(getApplicationContext(), "Попала в не ВАЛИДАТОР  "+ url, Toast.LENGTH_LONG).show();
            return false;
        }
        url_temp = url;
        if (pingHttp(url_temp)) return true;  // Простая прямая проверка на то что ввели в url
        url_temp = url.replace("https://","http://");
        if (pingHttp(url_temp)) return true;
        url_temp = url.replace("http://","https://");
        if (pingHttp(url_temp)) return true;


        return false;
    }
    public boolean  pingHttp(String url) {
        HttpURLConnection httpConnection ;
        try {
            httpConnection = (HttpURLConnection) new URL(url).openConnection();
            httpConnection.setRequestMethod("HEAD");
            if (httpConnection.getResponseCode() == 200){
                Toast.makeText(getApplicationContext(), "Сайт доступен  "+ url, Toast.LENGTH_LONG).show();
               editText.setText(url);
                return true;
            }
        } catch (IOException e) { }
        Toast.makeText(getApplicationContext(), "НЕ доступен  "+ url, Toast.LENGTH_SHORT).show();
        return false;
    }
    private class MyWebViewClient extends WebViewClient {
        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            return true;
        }
    }
}
