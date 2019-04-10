package site.app4web.ping;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
    private PingHttpTask pingHttpTask;
    String httpEdit;
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
        // прячем клавиатуру. view - это кнопка
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

             httpEdit = editText.getText().toString();
            if (tryHttp(httpEdit)) { // работает изучать
                editText.setText(httpEdit);
                Toast.makeText(getApplicationContext(), "!!!!!!!!Сайт доступен" + httpEdit, Toast.LENGTH_SHORT).show();
                webView.loadUrl(httpEdit);
            } else {
                Toast.makeText(getApplicationContext(), httpEdit +" Сайт НЕ доступен#######", Toast.LENGTH_SHORT).show();
            }
    }

    public boolean isConnected() {
        NetworkInfo ni = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }

    public  boolean tryHttp(String url) {
        Boolean answer = false;
        PingHttpTask pingHttpTask;

        List<PingHttpTask> listpingHttpTask = new ArrayList<PingHttpTask>();    // 1 лист задач
        ArrayList<String> Zapros= new ArrayList<String>();                      // 2 Лист сайтов

        // Если точно не САЙТ - в ПОИСК (false)
        if (!url.contains(".")) return false;
        url = url.trim();
        if (url.startsWith(".")) return false;
        if (url.endsWith(".")) return false;
        if (url.contains(" ")) return false;
        // Если WWW или м.б. сайт без HTTH то удалить приклеть и проверить Сайт или в Поиск
        url = url.toLowerCase();
        if (url.contains("www.")) {
             pingHttpTask = new PingHttpTask();
            listpingHttpTask.add(pingHttpTask);
            pingHttpTask.execute(url.replace("www.", ""));
            Zapros.add(url.replace("www.", ""));
        }

        if (url.startsWith("www.")){
             pingHttpTask = new PingHttpTask();
            listpingHttpTask.add(pingHttpTask);
            pingHttpTask.execute("http://"+ url);
            Zapros.add("http://"+ url);
        }
        if (url.startsWith("www.")){
           pingHttpTask = new PingHttpTask();
            listpingHttpTask.add(pingHttpTask);
            pingHttpTask.execute("https://"+ url);
            Zapros.add("https://"+ url);
        }
        if (url.startsWith("www.")){
            pingHttpTask = new PingHttpTask();
            listpingHttpTask.add(pingHttpTask);
            pingHttpTask.execute(url.replace("www.","http://"));
            Zapros.add(url.replace("www.","http://"));
        }
        if (url.startsWith("www.")) {
            pingHttpTask = new PingHttpTask();
            listpingHttpTask.add(pingHttpTask);
            pingHttpTask.execute(url.replace("www.","https://"));
            Zapros.add(url.replace("www.","http://"));
        }
        // Если сайт без HTTH то приклеть и проверить Сайт или в Поиск
        if (!url.startsWith("http")){
             pingHttpTask = new PingHttpTask();
            listpingHttpTask.add(pingHttpTask);
            pingHttpTask.execute("https://"+ url);
            Zapros.add("https://"+ url);
        }
        if (!url.startsWith("http")) {
             pingHttpTask = new PingHttpTask();
            listpingHttpTask.add(pingHttpTask);
            pingHttpTask.execute("http://"+ url);
            Zapros.add("http://"+ url);
        }

        // Если это все таки URL - проверка на правильность и http <--> https
        try { new URL(url);
        // Это наконец есть ли указанный "точно" сайт в Сети
             pingHttpTask = new PingHttpTask();
            listpingHttpTask.add(pingHttpTask);
            pingHttpTask.execute(url);  // Простая прямая проверка на то что ввели в url
            Zapros.add(url);
        if (url.startsWith("http:")) {
            pingHttpTask = new PingHttpTask();
            listpingHttpTask.add(pingHttpTask);
            pingHttpTask.execute(url.replace("http://", "https://"));
            Zapros.add(url.replace("http://", "https://"));
        }
        if (url.startsWith("https:")) {
            pingHttpTask = new PingHttpTask();
            listpingHttpTask.add(pingHttpTask);
            pingHttpTask.execute(url.replace("https://","http://" ));
            Zapros.add(url.replace("https://","http://" ));
        }
        } catch (MalformedURLException e) {
            Toast.makeText(getApplicationContext(), "Запрос не является URL: "+ url, Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(getApplicationContext(), "Выполняются параллельные проверки ="+ listpingHttpTask.size(), Toast.LENGTH_SHORT).show();
       // TimeUnit.SECONDS.sleep(1);
        try {
            for (PingHttpTask task : listpingHttpTask) {
                try {
                    answer = task.get(3, TimeUnit.SECONDS);
                    if (answer) return answer;
                } catch (TimeoutException e) {
                   task.cancel(true);
                }
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), "Закончились малые проверки ="+ listpingHttpTask.size(), Toast.LENGTH_SHORT).show();

       //String[] Zapros1 = Zapros.toArray(new String[Zapros.size()]);
       // String[] Zapros2 = Zapros.stream().toArray(String[]::new); // Java8 API 24
        pingHttpTask = new PingHttpTask();
        listpingHttpTask.add(pingHttpTask);
        pingHttpTask.execute(Zapros.toArray(new String[Zapros.size()]));
        Toast.makeText(getApplicationContext(), "Выполняется большая проверка шагов= "+ listpingHttpTask.size(), Toast.LENGTH_SHORT).show();
        try {
            try {
                answer = pingHttpTask.get(3, TimeUnit.SECONDS);
                return answer;
            } catch (TimeoutException e) {
                pingHttpTask.cancel(true);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;

    }

    public final class PingHttpTask extends AsyncTask<String, Void, Boolean> {


        //private URL url;
        private String temp;
        @Override
        protected void onPreExecute() {
         }
        @Override
        protected Boolean doInBackground(String... urls) {

            for (String url : urls) {
                HttpURLConnection httpConnection;
                try {
                    httpConnection = (HttpURLConnection) new URL(url).openConnection();
                    httpConnection.setRequestMethod("HEAD");
                    int code = httpConnection.getResponseCode();
                    if (code == 200) { // HttpURLConnection.HTTP_OK HttpURLConnection.HTTP_NOT_FOUND
                        temp = url;
                        httpEdit =url;
                        return true;
                    }
                } catch (IOException e) { }
            }
            temp = "catch "+httpEdit;
            return false;
        }
        @Override
        protected void onPostExecute(Boolean result) {
            Toast.makeText(getApplicationContext(), result+"  Подзадача  "+temp, Toast.LENGTH_SHORT).show();
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
}
