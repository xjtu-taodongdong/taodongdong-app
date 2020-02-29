package com.example.myfirstapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import android.os.AsyncTask;


import com.taodongdong.ecommerce.api.AbstractApiClient;
import com.taodongdong.ecommerce.api.ApiCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.BufferedInputStream;



public class MainActivity extends AbstractActivity {

    private ImageView imageView = null;
    private String urlpath = "https://taodongdong.ddltech.top/storage/avatar/demo.jpg";
    private MyAsyncTask mat = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView)findViewById(R.id.imageView);

        mat = new MyAsyncTask();
        Log.e("FUCK","o");
        mat.execute(urlpath);
        Log.e("FUCK","okkkk");

        client().call("Index.hello", null, new ApiCallback() {
            @Override
            public void onSuccess(Object data) {
                Log.e("SUCCESS 1", String.valueOf(data));
            }

            @Override
            public void onError(int code, String message, Object data) {
                Log.e("ERROR 1", message + ";" + data);
            }
        });

        client().call("Index.sudo", "SSS", new ApiCallback() {
            @Override
            public void onSuccess(Object data) {
                Log.e("SUCCESS 2", String.valueOf(data));
            }

            @Override
            public void onError(int code, String message, Object data) {
                Log.e("ERROR 2", message + ";" + data);
            }
        });
    }

    class MyAsyncTask extends AsyncTask<String, Void, Bitmap>{
        @Override
        //第一个会调用的方法
        protected void onPreExecute() {
            // 开始之前要做的准备操作在这里面执行
            super.onPreExecute();
        }

        @Override
        //第三个会调用的方法。用来展示处理的结果！
        // （当doInBackground方法完成异步处理之后会调用的方法）
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            /**
             * 将doInBackground（）方法中
             * 返回的bitmap解析的图片设置给ImageView
             * */
            Log.e("FUCK","okk");
            imageView.setImageBitmap(bitmap);
            Log.e("FUCK","okkk");
        }

        @Override
        //第二个会调用的方法。真正的耗时操作！下载网络图片
        protected Bitmap doInBackground(String... strings) {
            //获取传递进来的参数,取出对应的URL
            String path=strings[0];
            //定义网络连接对象
            HttpURLConnection connection;
            URL url = null;
            //获取需要的Bitmap
            Bitmap bitmap=null;
            //获取数据的输入流
            InputStream is;
            try {
                //获取网络连接对象
                url = new URL(path);
                connection=(HttpURLConnection) url.openConnection();
                //获取输入流
                is=connection.getInputStream();
                //包装下
                BufferedInputStream bis=new BufferedInputStream(is);
                //通过decodeStream（）方法解析输入流将输入流解析成Bitmap图片
                bitmap= BitmapFactory.decodeStream(bis);
                //关闭流
                is.close();
                bis.close();
                //捕获异常
            } catch (IOException e) {
                e.printStackTrace();
            }
            //返回的是解析后的网络图像
            Log.e("FUCK","ok");
            return bitmap;
        }
    }
}
