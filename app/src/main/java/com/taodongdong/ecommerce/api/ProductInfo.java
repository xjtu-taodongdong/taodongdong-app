package com.taodongdong.ecommerce.api;

public class ProductInfo {
    public int id;
    public int storeId;
    public int merchantUserId;
    public String productName;
    public int productPrice;
    public int productAmount;
    public String productDescription;
    public String productImage;

    /**
     * 获取商品价格（以元为单位） 不包含单位、前缀
     * @return 商品价格字符串
     */
    public String getProductPriceReadable() {
        return String.valueOf(((double)productPrice) / 100);
    }
}
