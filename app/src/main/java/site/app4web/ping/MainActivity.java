package site.app4web.ping;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
//  System.setProperty("sun.net.http.retryPost", "false")

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

// Master Branch 0.1
public class MainActivity extends AppCompatActivity {
    private WebView webView;
    EditText editText;
    TextView textView;
    String httpEdit;
    private PingHttpTask pingHttpTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.editText);
        textView = findViewById(R.id.textView);
        //StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

        // включаем поддержку JavaScript
        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());
        // указываем страницу загрузки
        //webView.loadUrl("http://developer.alexanderklimov.ru/android");
    }

    // https://xakep.ru/2015/06/11/coding-android-widget-site-availability/
    public void onClick1(View view) {
        hideKeyboard(view);     // прячем клавиатуру. view - это кнопка
        httpEdit = editText.getText().toString();

        ArrayList<String> Zapros = listTryHttp(httpEdit);
        if (tryHttp1(Zapros)) { // работает изучать
            editText.setText(httpEdit);
            textView.setText("!!!!!!!!Сайт доступен   " + httpEdit);
            Toast.makeText(getApplicationContext(), "!!!!!!!!Сайт доступен  " + httpEdit, Toast.LENGTH_SHORT).show();
            webView.loadUrl(httpEdit);
        } else {
            textView.setText(httpEdit + "  Сайт НЕ доступен#######");
            Toast.makeText(getApplicationContext(), httpEdit + " Сайт НЕ доступен#######", Toast.LENGTH_SHORT).show();
            webView.loadUrl("");
        }
    }

    public void onClick2(View view) {
        hideKeyboard(view);
        httpEdit = editText.getText().toString();
        ArrayList<String> Zapros = listTryHttp(httpEdit);
        if (tryHttp2(Zapros)) { // работает изучать
            editText.setText(httpEdit);
            textView.setText("!!!!!!!!Сайт доступен   " + httpEdit);
            Toast.makeText(getApplicationContext(), "!!!!!!!!Сайт доступен  " + httpEdit, Toast.LENGTH_SHORT).show();
            webView.loadUrl(httpEdit);
        } else {
            textView.setText(httpEdit + "  Сайт НЕ доступен#######");
            Toast.makeText(getApplicationContext(), httpEdit + " Сайт НЕ доступен#######", Toast.LENGTH_SHORT).show();
            webView.loadUrl("");

        }
    }

    public void onClick3(View view) {
        hideKeyboard(view);

    }

    public void onClick4(View view) {
        hideKeyboard(view);
        if (isConnected())
            Toast.makeText(getApplicationContext(), "Инет Есть", Toast.LENGTH_SHORT).show();
        else Toast.makeText(getApplicationContext(), "НЕТ Инет ", Toast.LENGTH_SHORT).show();
    }


    public boolean isConnected() {
        NetworkInfo ni = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }

    public ArrayList<String> listTryHttp(String url) {

        ArrayList<String> Zapros = new ArrayList<String>();                      // 2 Лист сайтов

        // Если точно не САЙТ - в ПОИСК (false)
        if (!url.contains(".")) return Zapros;
        url = url.trim().toLowerCase();
        if (url.startsWith(".")) return Zapros;
        if (url.endsWith(".")) return Zapros;
        if (url.contains(" ")) return Zapros;

        // Если это все таки URL - проверка на правильность и http <--> https
        try {
            new URL(url);
            // Это наконец есть ли указанный "точно" сайт в Сети
            Zapros.add(url);  // Простая прямая проверка на то что ввели в url
            if (url.startsWith("http:")) Zapros.add(url.replace("http://", "https://"));
            if (url.startsWith("https:")) Zapros.add(url.replace("https://", "http://"));
        } catch (MalformedURLException e) {
            Toast.makeText(getApplicationContext(), "Запрос не является URL: " + url, Toast.LENGTH_SHORT).show();
        }

        // Если сайт без HTTH то приклеть и проверить Сайт или в Поиск
        if (!url.startsWith("http")) Zapros.add("https://" + url);
        if (!url.startsWith("http")) Zapros.add("http://" + url);

        // Если WWW или м.б. сайт без HTTH то удалить приклеть и проверить Сайт или в Поиск
        if (url.contains("www.")) Zapros.add(url.replace("www.", ""));
        if (url.startsWith("www.")) Zapros.add("http://" + url);
        if (url.startsWith("www.")) Zapros.add("https://" + url);
        if (url.startsWith("www.")) Zapros.add(url.replace("www.", "http://"));
        if (url.startsWith("www.")) Zapros.add(url.replace("www.", "http://"));

        // TimeUnit.SECONDS.sleep(1);
        return Zapros;
    }

    public boolean tryHttp1(ArrayList<String> Zapros) {
        Toast.makeText(getApplicationContext(), "Стартуют малые проверки задач=" + Zapros.size(), Toast.LENGTH_LONG).show();
        // Порождает кучу задач по количеству в листе
        Boolean answer = false;
        PingHttpTask pingHttpTask;
        List<PingHttpTask> listpingHttpTask = new ArrayList<PingHttpTask>();    //  лист задач
        for (String Str : Zapros) {
            pingHttpTask = new PingHttpTask();
            listpingHttpTask.add(pingHttpTask);
            pingHttpTask.execute(Str);
        }
        // Проверяет кто из них жив true и  прибивает
        for (PingHttpTask task : listpingHttpTask) {
            try {
                if (task.get(3, TimeUnit.SECONDS)) answer = true;
            } catch (TimeoutException e) {
            } catch (ExecutionException e) {
            } catch (InterruptedException e) {
            }
            task.cancel(true);
        }
        return answer;
    }

    public boolean tryHttp2(ArrayList<String> Zapros) {
        boolean answer = false;
        Toast.makeText(getApplicationContext(), "Стартует большая проверка шагов= " + Zapros.size(), Toast.LENGTH_LONG).show();
        pingHttpTask = new PingHttpTask();
        pingHttpTask.execute(Zapros.toArray(new String[Zapros.size()]));
        try {
            try {
                answer = pingHttpTask.get(3, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                pingHttpTask.cancel(true);
            }
        } catch (ExecutionException e) {
        } catch (InterruptedException e) {
        }
        pingHttpTask.cancel(true);
        return answer;
    }

    public final class PingHttpTask extends AsyncTask<String, Void, Boolean> {
        HttpURLConnection httpConnection;
        //private URL url;
        private String temp;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            for (String url : urls) {
                try {
                    httpConnection = (HttpURLConnection) new URL(url).openConnection();
                    httpConnection.setRequestMethod("HEAD");
                   // httpConnection.setReadTimeout(10000);
                    int code = httpConnection.getResponseCode();
                    if (code == HttpURLConnection.HTTP_OK) { // HttpURLConnection.HTTP_OK (200) HttpURLConnection.HTTP_NOT_FOUND
                        temp = url;
                        httpConnection.disconnect();
                        return true;
                    }
                } catch (MalformedURLException e) {
                } catch (IOException e) {
                }
            }
            temp = "catch " + httpEdit;
            return false;
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) httpEdit=temp;
            try { httpConnection.disconnect(); } catch (Exception e) { }
            Toast.makeText(getApplicationContext(), result + "  Подзадача  " + temp, Toast.LENGTH_SHORT).show();
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            return true;
        }
    }

    private void hideKeyboard(View view) {
        textView.setText("");
        // прячем клавиатуру. view - это кнопка
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

    }
}
