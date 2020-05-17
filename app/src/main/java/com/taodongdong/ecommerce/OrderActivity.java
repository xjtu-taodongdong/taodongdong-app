package com.taodongdong.ecommerce;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.taodongdong.ecommerce.api.ApiCallback;
import com.taodongdong.ecommerce.api.OrderInfo;
import com.taodongdong.ecommerce.api.Page;
import com.taodongdong.ecommerce.prouctlistview.ProductItem;
import com.taodongdong.ecommerce.prouctlistview.ProductListAdapter;

import org.json.JSONException;

import java.io.File;
import java.util.Arrays;

public class OrderActivity extends AbstractActivity {

    public static final int BUY_ORDER = 0;
    public static final int SALE_ORDER = 1;

    int ID;
    ListView lv;
    ProductListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        lv = findViewById(R.id.order_list);
        adapter = new ProductListAdapter(this);
        lv.setAdapter(adapter);
        ApiCallback<Page<OrderInfo>> callback = new ApiCallback<Page<OrderInfo>>() {
            @Override
            public void onSuccess(Page<OrderInfo> data) throws JSONException {
                adapter.clear();
                ProductItem.Factory.convertFromOrderInfo(Arrays.asList(data.data),adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int code, String message, Object data) throws JSONException {

            }
        };
        Intent intent = getIntent();
        int type = intent.getIntExtra("type",0);
        if(type == BUY_ORDER){
            api().getPurchaserOrders(1,10,callback);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int orderID = adapter.getItem(position).getID();
                    confirmOrder(orderID);
                }
            });
        }else{
            api().getMerchantOrders(1,10,callback);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int orderID = adapter.getItem(position).getID();
                    sendOrder(orderID);
                }
            });
        }


    }
    void sendOrder(final int orderID){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("确认已经发货了吗");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                OrderActivity.this.api().sendOrder(orderID, new ApiCallback<String>() {
                    @Override
                    public void onSuccess(String data) throws JSONException {
                        OrderActivity.this.api().showToast("成功");
                    }

                    @Override
                    public void onError(int code, String message, Object data) throws JSONException {
                        OrderActivity.this.api().showToast("失败：" + message);
                    }
                });

                dialogInterface.cancel();
            }

        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }
    void confirmOrder(final int orderID){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("确认收到货了吗");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                OrderActivity.this.api().confirmOrder(orderID, new ApiCallback<String>() {
                    @Override
                    public void onSuccess(String data) throws JSONException {

                    }

                    @Override
                    public void onError(int code, String message, Object data) throws JSONException {
                        OrderActivity.this.api().showToast("失败：" + message);
                    }
                });

                dialogInterface.cancel();
            }

        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }
}
