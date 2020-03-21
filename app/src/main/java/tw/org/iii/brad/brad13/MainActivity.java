package tw.org.iii.brad.brad13;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private ConnectivityManager cmgr;
    private MyReceiver myReceiver;
    private TextView mesg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mesg = findViewById(R.id.mesg);

        cmgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION); // Action
        //filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction("brad");
        registerReceiver(myReceiver, filter);
    }

    @Override
    public void finish() {
        unregisterReceiver(myReceiver);
        super.finish();
    }

    private boolean isConnectNetwork(){
        NetworkInfo networkInfo = cmgr.getActiveNetworkInfo();
        return networkInfo!=null && networkInfo.isConnectedOrConnecting();
    }

    private boolean isWifiConnected(){
        NetworkInfo networkInfo = cmgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo.isConnected();

    }

    public void test1(View view){
        Log.v("brad", "isNetwork = "  + isConnectNetwork());
    }

    public void test2(View view){
        Log.v("brad", "isWifi = "  + isWifiConnected());
    }

    public void test3(View view){
        new Thread(){
            @Override
            public void run() {
                try {
                    URL url = new URL("https://bradchao.com/wp");  // http => Android 8+ useClearTextTraffic = true
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();

                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(conn.getInputStream()));
                    String line; StringBuffer sb = new StringBuffer();
                    while ( (line = reader.readLine()) != null){
                        sb.append(line + "\n");
                    }
                    reader.close();

                    Intent intent = new Intent("brad");
                    intent.putExtra("data", sb.toString());
                    sendBroadcast(intent);  // Context => Activity, Service, Application

                }catch (Exception e){
                    Log.v("brad", e.toString());
                }
            }
        }.start();
    }









    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.v("brad", "onReceive");
            if (intent.getAction().equals("brad")){
                String data = intent.getStringExtra("data");
                mesg.setText(data);
            }else if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                test1(null);
            }



        }
    }


}
