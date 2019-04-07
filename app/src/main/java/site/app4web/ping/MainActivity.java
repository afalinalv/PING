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

/*import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
*/
import java.io.IOException;
import java.net.HttpURLConnection;
//import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;

//import javax.net.ssl.HttpsURLConnection;

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
        // try {

            // in case of Linux change the 'n' to 'c' не работает
           // Process p1 = java.lang.Runtime.getRuntime().exec("ping -n 1 www.google.com");
          //  int returnVal = p1.waitFor();
           // if  (returnVal==0) {
       // Toast.makeText(getApplicationContext(), "isConnectedРее2(String http)="+ isConnectedРее2(TryText), Toast.LENGTH_SHORT).show();
       // Toast.makeText(getApplicationContext(), "isConnectedРее3(String http)="+ isConnectedРее3(TryText), Toast.LENGTH_SHORT).show();
       // Toast.makeText(getApplicationContext(), "InterruptedException", Toast.LENGTH_SHORT).show();




            if (isServerReachable(TryText)) { // работает изучать
         //   if (InetAddress.getByName(TryText).isReachable(5000)) { // не работает excepion
                Toast.makeText(getApplicationContext(), "Сайт доступен", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Сайт НЕ доступен", Toast.LENGTH_SHORT).show();
            }
       /* } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Exception", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "InterruptedException", Toast.LENGTH_SHORT).show();
        }*/
    }


    public boolean isConnected() {

        NetworkInfo ni = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();

        //ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }
/*
    public boolean isConnectedРее2(String http){
// сразу на вылет
    DefaultHttpClient httpClient;
        httpClient = new DefaultHttpClient();
        HttpGet request = new HttpGet(http);
        HttpResponse response = null;
        try {
            response = httpClient.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        int responseCode = response.getStatusLine().getStatusCode();
    if(responseCode==200) return false;
        return true;
     // url найден
    }

    public boolean isConnectedРее3(String http){
        // Сразу на вылет
        HttpClient httpClient;
        httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(http);
        HttpResponse response = null;
        try {
            response = httpClient.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        int responseCode = response.getStatusLine().getStatusCode();
        if(responseCode==200)  return false;
        return true;
        // url найден
    }*/

    public static boolean isServerReachable(String url) {
       /** StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);*/
        boolean reachable = false;
        HttpURLConnection httpConnection ;
        try {
            // Работает на http и https и http://www.
            httpConnection = (HttpURLConnection)  new URL(url).openConnection();
           /* if (url.toLowerCase().contains("https".toLowerCase())) {
           // работает только на https а на http вообще вылетает мимо TRY
                httpConnection = (HttpsURLConnection) new URL(url).openConnection();
            } else {
                httpConnection = (HttpURLConnection)  new URL(url).openConnection();
            }
            */
            httpConnection.setRequestMethod("HEAD");
            if (httpConnection.getResponseCode() == 200) {
                reachable = true;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reachable;
    }

}
/*
        val url = URL(path)
        if (path.toLowerCase().contains("https".toLowerCase())) {
            httpConnection = url.openConnection() as HttpsURLConnection
        } else {
            httpConnection = url.openConnection() as HttpURLConnection
        }
        httpConnection.requestMethod = "GET"
        httpConnection.readTimeout = 10000
        httpConnection.connect()
        reader = BufferedReader(InputStreamReader(httpConnection.inputStream))
 */