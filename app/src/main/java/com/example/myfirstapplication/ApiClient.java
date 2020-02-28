package com.example.myfirstapplication;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiClient {
    protected OkHttpClient okHttpClient;
    public static final String RPC_ENTRY = "https://taodongdong.ddltech.top/api/v1/rpc";
    protected static final MediaType JSONType = MediaType.parse("application/json; charset=utf-8");

    public ApiClient() {
        okHttpClient = new OkHttpClient();
    }

    public void call(String action, JSONObject data) throws JSONException {
        JSONObject payload = new JSONObject();
        payload.put("action", action);
        payload.put("data", data);
        RequestBody body = RequestBody.create(JSONType, String.valueOf(payload));
        Request request = new Request.Builder().url(RPC_ENTRY).method("POST", body).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();
                Log.w("API_CLIENT", data);
            }
        });
    }
}

