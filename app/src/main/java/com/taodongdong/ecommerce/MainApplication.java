package com.taodongdong.ecommerce;

import android.app.Application;
import android.util.Log;

import com.taodongdong.ecommerce.api.ApiClient;

public class MainApplication extends Application {
    protected ApiClient apiClient = null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        try {
            apiClient = new ApiClient(this);
            apiClient.initHandlerOnCurrentThread();
        } catch (RuntimeException e) {
            Log.e("TAODONGDONG_APPLICATION", "ON CREATE, Can not create api client");
            e.printStackTrace();
        }
    }
    static MainApplication instance = null;
    public static MainApplication getApp(){
        return instance;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }
}
