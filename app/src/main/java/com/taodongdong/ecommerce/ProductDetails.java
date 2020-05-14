package com.taodongdong.ecommerce;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.taodongdong.ecommerce.api.ApiCallback;
import com.taodongdong.ecommerce.api.ProductInfo;
import com.taodongdong.ecommerce.prouctlistview.ProductItem;
import com.taodongdong.ecommerce.prouctlistview.ProductListAdapter;

import org.json.JSONException;

import java.io.InputStream;

public class ProductDetails extends AbstractActivity {

    int authority = 0; //默认点击图片用户权限是买家
    Button purchase;
    Button modify;
    ImageView imageView;
    TextView name;
    TextView amount;
    TextView detail;
    TextView price;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        purchase = (Button) findViewById(R.id.purchase);
        modify = (Button) findViewById(R.id.modify);
        imageView = (ImageView) findViewById(R.id.ProductIcon);
        name = (TextView)findViewById(R.id.productName);
        amount = (TextView)findViewById(R.id.remainingNum);
        detail = (TextView) findViewById(R.id.detailOfProduct);
        price = (TextView) findViewById(R.id.priceOfProduct);
        if(authority==1){
            purchase.setVisibility(View.GONE); //商家权限，隐藏购买按钮
        }else {
            modify.setVisibility(View.GONE); //用户权限，隐藏购买按钮
        }
        int ID = savedInstanceState.getInt("id");
        api().getProductInfo(ID, new ApiCallback<ProductInfo>() {
            @Override
            public void onSuccess(ProductInfo data) throws JSONException {
                ImageGetter ig = new ImageGetter(imageView);
                ig.execute(data.productImage);
                name.setText(data.productName);
                amount.setText(String.valueOf(data.productAmount));
                detail.setText(data.productDescription);
                price.setText(data.getProductPriceReadable());
            }

            @Override
            public void onError(int code, String message, Object data) throws JSONException {

            }
        });

    }
}
class ImageGetter extends AsyncTask<String, Integer, Bitmap> {

    ImageView iv;
    public ImageGetter(ImageView imageView){
        iv = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... objs) {
        Bitmap image = null;
        try {
            InputStream is = new java.net.URL((String)objs[0]).openStream();
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        iv.setImageBitmap(bitmap);
    }
}
