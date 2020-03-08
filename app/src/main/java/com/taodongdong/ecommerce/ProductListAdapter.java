package com.example.helloworld;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IdRes;

import java.util.List;

public class ProductListAdapter extends ArrayAdapter<ProductItem> {
    ProductListAdapter(Context context,List<ProductItem> objects){
        super(context,R.layout.list_item_layout,objects);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ProductItem item = getItem(position); //获取当前项的Fruit实例
        View view= LayoutInflater.from(getContext()).inflate(R.layout.list_item_layout,parent,false);
        ImageView image = (ImageView) view.findViewById(R.id.item_image);
        TextView name =(TextView) view.findViewById(R.id.item_name);
        image.setImageResource(item.getImage());
        name.setText(item.getDescription());
        String str = name.getText().toString();
        return view;
    }
}
