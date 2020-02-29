package com.taodongdong.ecommerce;

import androidx.appcompat.app.AppCompatActivity;

public class AbstractActivity extends AppCompatActivity {
    public ApiClient client() {
        MainApplication app = (MainApplication) getApplication();
        return app.getApiClient();
    }
}
