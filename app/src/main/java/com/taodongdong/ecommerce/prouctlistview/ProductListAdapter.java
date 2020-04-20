package com.taodongdong.ecommerce.prouctlistview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.taodongdong.ecommerce.MainApplication;
import com.taodongdong.ecommerce.R;
import com.taodongdong.ecommerce.prouctlistview.ProductItem;

import java.util.ArrayList;
import java.util.List;

public class ProductListAdapter extends ArrayAdapter<ProductItem> {
    public static Bitmap defaultImage = BitmapFactory.decodeResource(MainApplication.getApp().getResources(), R.drawable.taodongdong);
    public ProductListAdapter(Context context,List<ProductItem> objects){
        super(context, R.layout.list_item_layout,objects);
    }
    public ProductListAdapter(Context context){
        super(context, R.layout.list_item_layout, new ArrayList<ProductItem>());
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ProductItem item = getItem(position);
        View view= LayoutInflater.from(getContext()).inflate(R.layout.list_item_layout,parent,false);
        ImageView image = (ImageView) view.findViewById(R.id.item_image);
        TextView name =(TextView) view.findViewById(R.id.item_name);
        image.setImageBitmap(item.getImage());
        name.setText(item.getDescription());
        String str = name.getText().toString();
        return view;
    }
}
