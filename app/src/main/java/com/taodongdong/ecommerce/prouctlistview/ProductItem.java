package com.taodongdong.ecommerce.prouctlistview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.taodongdong.ecommerce.MainApplication;
import com.taodongdong.ecommerce.R;
import com.taodongdong.ecommerce.api.OrderInfo;
import com.taodongdong.ecommerce.api.ProductInfo;

import java.io.InputStream;
import java.util.List;

public class ProductItem {
    private String description;

    public void setImage(Bitmap image) {
        this.image = image;
    }

    private Bitmap image;

    private int id;


    public ProductItem(String description, Bitmap image) {
        this.description = description;
        this.image = image;
    }

    public ProductItem(ProductInfo pi){
        this.description = pi.productDescription;
    }

    public static class Factory{
        public static void  convertFromProductInfo(List<ProductInfo> list, ProductListAdapter adapter){
            adapter.clear();
            for(int i = 0; i < list.size(); i++){
                ProductItem item = new ProductItem(list.get(i).productDescription, adapter.defaultImage);
                item.id = list.get(i).id;
                adapter.add(item);
                Log.i("TDD", "image getter initing ");
                ImageGetter ig = new ImageGetter(adapter, item);
                ig.execute(list.get(i).productImage);
            }

        }
        public static void  convertFromOrderInfo(List<OrderInfo> list, ProductListAdapter adapter){
            adapter.clear();
            for(int i = 0; i < list.size(); i++){
                OrderInfo info = list.get(i);
                String txt = String.format("状态：%s,买家：%d,:%s",info.getstatus(),info.purchaserUserId,info.productDescription);
                ProductItem item = new ProductItem(txt, adapter.defaultImage);
                item.id = list.get(i).id;
                adapter.add(item);

                ImageGetter ig = new ImageGetter(adapter, item);
                ig.execute(info.productImage);
            }

        }
    }
    public int getID(){ return id; }

    public String getDescription() {
        return description;
    }

    public Bitmap getImage() {
        return image;
    }
}



class ImageGetter extends AsyncTask<String, Integer, Bitmap>{

    ProductListAdapter list = null;
    ProductItem item = null;
    public ImageGetter(ProductListAdapter list, ProductItem item){
        this.list = list;
        this.item = item;
    }

    @Override
    protected Bitmap doInBackground(String... objs) {
        Bitmap image = null;
        try {
            Log.i("TDD", "image getting "+ (String)objs[0]);
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
        item.setImage(bitmap);
        list.notifyDataSetChanged();
    }
}
