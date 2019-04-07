package site.app4web.ping;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
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
    }
    // https://xakep.ru/2015/06/11/coding-android-widget-site-availability/
    public void onClick(View view) {
        String TryText = editText.getText().toString();
            if (tryHttp(TryText)) { // работает изучать
                Toast.makeText(getApplicationContext(), "Сайт доступен", Toast.LENGTH_SHORT).show();
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
        url = url.toLowerCase().trim();
        String url_temp = url;
        if (pingHttp(url_temp)) return true;  // Простая прямая проверка на то что ввели в url
        url_temp = url.replace("https://","http://");
        if (pingHttp(url_temp)) return true;
        url_temp = url.replace("http://","https://");
        if (pingHttp(url_temp)) return true;
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
}
