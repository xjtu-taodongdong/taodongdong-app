package com.taodongdong.ecommerce;

import android.app.Application;
import android.util.Log;

import com.taodongdong.ecommerce.api.ApiClient;

public class MainApplication extends Application {
    protected ApiClient apiClient = null;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            apiClient = new ApiClient(this);
        } catch (RuntimeException e) {
            Log.e("TAODONGDONG_APPLICATION", "ON CREATE, Can not create api client");
            e.printStackTrace();
        }
    }

    public ApiClient getApiClient() {
        return apiClient;
    }
}
