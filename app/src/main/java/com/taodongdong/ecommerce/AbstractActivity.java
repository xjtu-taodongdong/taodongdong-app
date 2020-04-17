package com.taodongdong.ecommerce;

import androidx.appcompat.app.AppCompatActivity;

import com.taodongdong.ecommerce.api.ApiClient;

public class AbstractActivity extends AppCompatActivity {
    public ApiClient client() {
        MainApplication app = (MainApplication) getApplication();
        return app.getApiClient();
    }
}
