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
//  System.setProperty("sun.net.http.retryPost", "false")
import java.io.IOException;
//import java.net.HttpURLConnection;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
// AsynkTask branch 1
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
        // Если точно не САЙТ - в ПОИСК
        if (!url.contains(".")) return false;
        url = url.trim();
        if (url.startsWith(".")) return false;
        if (url.endsWith(".")) return false;
        if (url.contains(" ")) return false;
        // Если WWW или м.б. сайт без HTTH то удалить приклеть и проверить Сайт или в Поиск
        url = url.toLowerCase();
        if (url.contains("www."))    if (pingHttp(url.replace("www.",""))) return true;
        if (url.startsWith("www."))  if (pingHttp("http://"+ url)) return true;
        if (url.startsWith("www."))  if (pingHttp("https://"+ url)) return true;
        if (url.startsWith("www."))  if (pingHttp(url.replace("www.","http://"))) return true;
        if (url.startsWith("www."))  if (pingHttp(url.replace("www.","https://"))) return true;
        if (!url.startsWith("http")) if (pingHttp("https://"+ url)) return true;
        if (!url.startsWith("http")) if (pingHttp("http://"+ url)) return true;

        // Если это все таки URL - проверка на правильность и http <--> https
        try { new URL(url); } catch (MalformedURLException e) {
            Toast.makeText(getApplicationContext(), "Попала в MalformedURLException  "+ url, Toast.LENGTH_LONG).show();
            return false;
        }
        // Это наконец есть ли указанный "точно" сайт в Сети
        if (pingHttp(url)) return true;  // Простая прямая проверка на то что ввели в url
        if (url.startsWith("http:"))  if (pingHttp(url.replace("http://", "https://"))) return true;
        if (url.startsWith("https:")) if (pingHttp(url.replace("https://","http://" ))) return true;

        return false;
    }
    public boolean  pingHttp(String url) {
        HttpURLConnection httpConnection ;
        try {
            httpConnection = (HttpURLConnection) new URL(url).openConnection();
            httpConnection.setRequestMethod("HEAD");
            int code = httpConnection.getResponseCode();
            if (code == 200){
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
