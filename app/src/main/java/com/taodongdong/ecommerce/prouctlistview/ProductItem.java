package com.taodongdong.ecommerce.prouctlistview;

import android.graphics.Bitmap;

public class ProductItem {
    private String description;
    private Bitmap image;


    public ProductItem(String description, Bitmap image) {
        this.description = description;
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public Bitmap getImage() {
        return image;
    }
}
