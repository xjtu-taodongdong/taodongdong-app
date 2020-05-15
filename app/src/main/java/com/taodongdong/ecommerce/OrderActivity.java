package com.taodongdong.ecommerce;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import com.taodongdong.ecommerce.api.ApiCallback;
import com.taodongdong.ecommerce.api.OrderInfo;
import com.taodongdong.ecommerce.api.Page;
import com.taodongdong.ecommerce.prouctlistview.ProductItem;
import com.taodongdong.ecommerce.prouctlistview.ProductListAdapter;

import org.json.JSONException;

import java.util.Arrays;

public class OrderActivity extends AbstractActivity {

    public static final int BUY_ORDER = 0;
    public static final int SALE_ORDER = 1;
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
        int type = savedInstanceState.getInt("type");
        if(type == BUY_ORDER){
            api().getPurchaserOrders(1,10,callback);
        }else{
            api().getMerchantOrders(1,10,callback);
        }
    }
}
