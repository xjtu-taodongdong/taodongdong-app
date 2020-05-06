package com.taodongdong.ecommerce;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.graphics.BitmapFactory;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.os.AsyncTask;
import android.widget.Toast;


import com.taodongdong.ecommerce.api.ApiCallback;
import com.taodongdong.ecommerce.api.ApiClient;
import com.taodongdong.ecommerce.api.Errors;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.BufferedInputStream;


public class MainActivity extends AbstractActivity {

    private ImageView imageView = null;
    private String urlpath = "https://taodongdong.ddltech.top/storage/avatar/demo.jpg";
    private MyAsyncTask mat = null;
    private Button log = null;
    private Button register = null;
    private EditText username = null;
    private EditText password = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView)findViewById(R.id.imageView);
        log = (Button)findViewById(R.id.login);
        register = (Button)findViewById(R.id.register);
        username = (EditText)findViewById(R.id.editText);
        password = (EditText)findViewById(R.id.editText2);
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usr = username.getText().toString();
                String pwd = password.getText().toString();
                api().login(usr, pwd, new ApiCallback<String>() {
                    @Override
                    public void onSuccess(String data) throws JSONException {
                        Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                        startActivity(intent);
                    }
                    @Override
                    public void onError(int code, String message, Object data) throws JSONException {
                        switch (code){
                            case Errors.NO_SUCH_USER:
                                api().showToast("无此用户");
                                break;
                            case Errors.PASSWORD_ERROR:
                                api().showToast("密码错误");
                                break;
                            case Errors.CANT_CREATE_HASH:
                                api().showToast("无法建立哈希");
                                break;
                        }

                    }
                });

            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,RegisterPage.class);
                startActivity(intent);
            }
        });

        mat = new MyAsyncTask();
        mat.execute(urlpath);

//        api().call("Index.hello", null, new ApiCallback() {
//            @Override
//            public void onSuccess(Object data) {
//                Log.e("SUCCESS 1", String.valueOf(data));
//            }
//
//            @Override
//            public void onError(int code, String message, Object data) {
//                Log.e("ERROR 1", message + ";" + data);
//            }
//        });
//
//        api().call("Index.sudo", "SSS", new ApiCallback() {
//            @Override
//            public void onSuccess(Object data) {
//                Log.e("SUCCESS 2", String.valueOf(data));
//            }
//
//            @Override
//            public void onError(int code, String message, Object data) {
//                Log.e("ERROR 2", message + ";" + data);
//            }
//        });
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
            imageView.setImageBitmap(bitmap);
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
            return bitmap;
        }
    }
}
