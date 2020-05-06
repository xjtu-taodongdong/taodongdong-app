package com.taodongdong.ecommerce;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ProductDetails extends AppCompatActivity {

    int authority = 0; //默认点击图片用户权限是买家
    Button purchase;
    Button modify;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        purchase = (Button) findViewById(R.id.purchase);
        modify = (Button) findViewById(R.id.modify);
        if(authority==1){
            purchase.setVisibility(View.GONE); //商家权限，隐藏购买按钮
        }else {
            modify.setVisibility(View.GONE); //用户权限，隐藏购买按钮
        }

    }
}
