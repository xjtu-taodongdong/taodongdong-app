package com.taodongdong.ecommerce.api;

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

public abstract class AbstractApiClient {
    protected OkHttpClient okHttpClient;
    public static final String RPC_ENTRY = "https://taodongdong.ddltech.top/api/v1/rpc";
    protected static final MediaType JSONType = MediaType.parse("application/json; charset=utf-8");

    public AbstractApiClient() {
        okHttpClient = new OkHttpClient();
    }

//    public void call(String action, JSONObject data, ApiCallback callback) {
//        try {
//            JSONObject payload = new JSONObject();
//            payload.put("action", action);
//            payload.put("data", data);
//            sendRequest(String.valueOf(payload), callback);
//        } catch (JSONException e) {
//            onRequestJSONException(e);
//        }
//    }
//
//    public void call(String action, boolean data, ApiCallback callback) {
//        try {
//            JSONObject payload = new JSONObject();
//            payload.put("action", action);
//            payload.put("data", data);
//            sendRequest(String.valueOf(payload), callback);
//        } catch (JSONException e) {
//            onRequestJSONException(e);
//        }
//    }
//
//    public void call(String action, double data, ApiCallback callback) {
//        try {
//            JSONObject payload = new JSONObject();
//            payload.put("action", action);
//            payload.put("data", data);
//            sendRequest(String.valueOf(payload), callback);
//        } catch (JSONException e) {
//            onRequestJSONException(e);
//        }
//    }
//
//    public void call(String action, int data, ApiCallback callback) {
//        try {
//            JSONObject payload = new JSONObject();
//            payload.put("action", action);
//            payload.put("data", data);
//            sendRequest(String.valueOf(payload), callback);
//        } catch (JSONException e) {
//            onRequestJSONException(e);
//        }
//    }
//
//    public void call(String action, long data, ApiCallback callback) {
//        try {
//            JSONObject payload = new JSONObject();
//            payload.put("action", action);
//            payload.put("data", data);
//            sendRequest(String.valueOf(payload), callback);
//        } catch (JSONException e) {
//            onRequestJSONException(e);
//        }
//    }
//
//    public void call(String action, Object data, ApiCallback callback) {
//        try {
//            JSONObject payload = new JSONObject();
//            payload.put("action", action);
//            payload.put("data", data);
//            sendRequest(String.valueOf(payload), callback);
//        } catch (JSONException e) {
//            onRequestJSONException(e);
//        }
//    }

    protected JSONObject forAction(String action) throws JSONException {
        JSONObject payload = new JSONObject();
        payload.put("action", action);
        return payload;
    }

    protected void sendRequest(String action, JSONObject data, ApiCallback<Object> callback) {
        try {
            sendRequest(forAction(action).put("data", data), callback);
        } catch (JSONException e) {
            onRequestJSONException(e);
        }
    }

    protected void sendRequest(JSONObject payload, ApiCallback<Object> callback) {
        sendRequest(String.valueOf(payload), callback);
    }

    protected void sendRequest(String payload, final ApiCallback<Object> callback) {
        RequestBody body = RequestBody.create(JSONType, payload);
        Request request = new Request.Builder().url(RPC_ENTRY).method("POST", body).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onIOException(e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    String dataString = response.body().string();
                    try {
                        JSONObject result = new JSONObject(dataString);
                        try {
                            int code = result.getInt("code");
                            String message = result.optString("message");
                            Object data = result.opt("data");
                            if (code == 0) {
                                callback.onSuccess(data);
                            } else {
                                callback.onError(code, message, data);
                            }
                        } catch (JSONException e) {
                            onResponseFormatInvalid(result, e);
                        }
                    } catch (JSONException e) {
                        onResponseJSONException(e);
                    }
                } catch (IOException e) {
                    onIOException(e);
                }
            }
        });
    }

    public abstract void onIOException(IOException e);

    public abstract void onRequestJSONException(JSONException e);

    public abstract void onResponseJSONException(JSONException e);

    public abstract void onResponseFormatInvalid(JSONObject response, JSONException e);
}
