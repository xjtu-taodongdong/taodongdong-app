package com.taodongdong.ecommerce;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.os.AsyncTask;
import android.widget.Toast;
import android.app.Activity;
import android.os.Build;
import android.os.Environment;

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


    private Button log = null;
    private Button register = null;
    private EditText username = null;
    private EditText password = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                        intent.putExtra("tab","0");
                        startActivity(intent);
                        MainActivity.this.finish();
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

}
