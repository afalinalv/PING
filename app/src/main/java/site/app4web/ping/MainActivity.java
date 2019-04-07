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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        if (isConnected())
            Toast.makeText(getApplicationContext(), "Инет Есть", Toast.LENGTH_SHORT).show();
        else Toast.makeText(getApplicationContext(), "НЕТ Инет ", Toast.LENGTH_SHORT).show();
    }
    // https://xakep.ru/2015/06/11/coding-android-widget-site-availability/
    public void onClick(View view) {
        EditText editText = findViewById(R.id.editText);
        String TryText = editText.getText().toString();

            if (isServerReachable(TryText)) { // работает изучать
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

    public static boolean isServerReachable(String url) {
        boolean reachable = false;
        HttpURLConnection httpConnection ;
        try {
            httpConnection = (HttpURLConnection)  new URL(url).openConnection();
        httpConnection.setRequestMethod("HEAD");
            if (httpConnection.getResponseCode() == 200) {
                reachable = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reachable;
    }
}
