package com.example.administrator.app2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;


public class MainActivity extends AppCompatActivity implements Runnable{
    float dollarRate = 1;
    float euroRate = 1;
    float wonRate = 1;
    EditText rmb;
    TextView result;
    String rmb1 = null;
    String TAG = "rate";
    String update_time;
    String now_time;
   SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   int day=0;


    Handler handler;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rmb = (EditText) findViewById(R.id.rmb);
        result = (TextView) findViewById(R.id.result);

        now_time=df.format(System.currentTimeMillis());

        //从文件中读取数据
        SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
        dollarRate = sharedPreferences.getFloat("dollar_rate", 0.0f);
        euroRate = sharedPreferences.getFloat("euro_rate", 0.0f);
        wonRate = sharedPreferences.getFloat("won_rate", 0.0f);
       update_time=sharedPreferences.getString("update_time", null);
       //update_time="2018-10-9 13:18:59";
        Log.i(TAG,"上次更新时间："+update_time);

        try
        {
            Date date1 = df.parse(update_time);
            Date date2= df.parse(now_time);
            day=(int) ((date2.getTime() - date1.getTime()) / (1000*3600*24));
            Log.i(TAG,"日期之差："+day);
        } catch (ParseException e) {
            e.printStackTrace();
        }

            if(day>=1) {
                Thread t = new Thread(this);
                t.start();

                handler = new Handler() {
                    public void handleMessage(Message msg) {
                        if (msg.what == 5) {
                            Bundle bdl = (Bundle) msg.obj;
                            dollarRate = bdl.getFloat("dollar-rate");
                            euroRate = bdl.getFloat("euro-rate");
                            wonRate = bdl.getFloat("won-rate");
                            Log.i(TAG, "handleMessage: dollarRate:" + dollarRate);
                            Log.i(TAG, "handleMessage: euroRate:" + euroRate);
                            Log.i(TAG, "handleMessage: wonRate:" + wonRate);
                            //  Toast.makeText(MainActivity.this, "汇率已更新", Toast.LENGTH_SHORT).show();
                            SharedPreferences sp = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putFloat("dollar_rate", dollarRate);
                            editor.putFloat("euro_rate", euroRate);
                            editor.putFloat("won_rate", wonRate);
                            editor.putString("update_time", now_time);
                            editor.apply();
                        }
                        super.handleMessage(msg);
                    }
                };
            }
        }

    public void run() {   //在子线程里给主线程返回消息
        for (int i = 1; i < 3; i++) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Bundle bundle = new Bundle();
        //获取网络数据
            Document doc = null;
            try {
                String url = "http://www.usd-cny.com/bankofchina.htm";
                doc = Jsoup.connect(url).get();
                Log.i(TAG, "run: " + doc.title());

                Elements table = doc.getElementsByTag("table");
                Elements trs = table.select("tr");
                for (int i = 1; i < trs.size(); i++) {

                    Elements tds = trs.get(i).select("td");
                    Element td1 = tds.get(0);
                    Element td2 = tds.get(5);
                    String str1 = td1.text();
                    String val = td2.text();
                    float v = 100f / Float.parseFloat(val);
                    if ("美元".equals(str1)) {
                        bundle.putFloat("dollar-rate", v);
                        Log.i(TAG, "美元" + v);
                    } else if ("欧元".equals(str1)) {
                        bundle.putFloat("euro-rate", v);
                        Log.i(TAG, "欧元" + v);
                    } else if ("韩国元".equals(str1)) {
                        bundle.putFloat("won-rate", v);
                        Log.i(TAG, "韩元" + v);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Message msg = handler.obtainMessage(5);
            msg.obj = bundle;
            handler.sendMessage(msg);

    }

    protected void onActivityResult(int requestCode, int resultCode,Intent data)
    {
        if(requestCode==1 && resultCode==2){
            Bundle bundle = data.getExtras();
            dollarRate = bundle.getFloat("key_dollar",0.1f);
            euroRate = bundle.getFloat("key_euro",0.1f);
            wonRate = bundle.getFloat("key_won",0.1f);
            Log.i(TAG,"获取到的美元汇率："+dollarRate);
            Log.i(TAG,"获取到的欧元汇率："+euroRate);
            Log.i(TAG,"获取到的韩元元汇率："+wonRate);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

   public void change(View view){
       rmb1 = rmb.getText().toString();
       if(rmb1.isEmpty())
       {
           Toast.makeText(this,"输入不能为空",Toast.LENGTH_SHORT).show();
       }
       else {
           float rmb2 = Float.parseFloat(rmb1);
           switch (view.getId()) {
               case R.id.dollar:
                   result.setText(String.valueOf(rmb2 * dollarRate));
                   break;
               case R.id.ouyuan:
                   result.setText(String.valueOf(rmb2 * euroRate));
                   break;
               case R.id.hanyuan:
                   result.setText(String.valueOf(rmb2 * wonRate));
                   break;
           }
       }
   }

    public void set(View btn){
        Intent config = new Intent(this,ConfigActivity.class);
        config.putExtra("dollar_rate_key",dollarRate);
        config.putExtra("euro_rate_key",euroRate);
        config.putExtra("won_rate_key",wonRate);
        //startActivity(config);
        startActivityForResult(config,1);
    }
    public void show(View btn){
        Intent mylists = new Intent(this,Lists.class);

        startActivity(mylists);
    }
    public void grid(View view){
        Intent mygrid = new Intent(this,gridView.class);
        startActivity(mygrid);
    }
}

