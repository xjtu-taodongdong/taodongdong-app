package com.taodongdong.ecommerce;

import android.content.Context;
import android.widget.Toast;

import com.taodongdong.ecommerce.api.AbstractApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ApiClient extends AbstractApiClient {
    protected  Context context;

    public ApiClient(Context context) {
        this.context = context;
    }

    @Override
    public void onIOException(IOException e) {
        Toast.makeText(context, "网络异常：" + e.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestJSONException(JSONException e) {
        Toast.makeText(context, "请求体JSON异常：" + e.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponseJSONException(JSONException e) {
        Toast.makeText(context, "响应体JSON异常：" + e.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponseFormatInvalid(JSONObject response, JSONException e) {
        Toast.makeText(context, "响应体JSON格式不正确：" + e.getMessage(), Toast.LENGTH_LONG).show();
    }
}
