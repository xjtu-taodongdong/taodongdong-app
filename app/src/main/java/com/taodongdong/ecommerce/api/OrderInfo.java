package com.taodongdong.ecommerce.api;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class OrderInfo {
    public int id;
    /**
     * 商品ID
     */
    public int productId;
    /**
     * 商铺ID
     */
    public int storeId;
    /**
     * 买家用户ID
     */
    public int purchaserUserId;
    /**
     * 卖家用户ID
     */
    public int merchantUserId;
    public String productName;
    public int productPrice;
    public int productAmount;
    public String productDescription;
    public String productImage;
    public int orderStatus;

    /**
     * 已经创建订单但是还没有支付
     */
    public final static int STATUS_UNPAID = 1;
    /**
     * 已经支付但是卖家没有发货
     */
    public final static int STATUS_UNSENT = 2;
    /**
     * 已经发货但是客户还没有收货
     */
    public final static int STATUS_UNDELIVERED = 3;
    /**
     * 客户已经确认收货
     */
    public final static int STATUS_CONFIRMED = 4;

    /**
     * 从JSONObject读取一个Order
     * @param d 输入
     * @return 输出
     * @throws JSONException
     */
    public static OrderInfo fromJSONObject(JSONObject d) throws JSONException {

        OrderInfo o = new OrderInfo();
        o.id = d.getInt("id");
        o.productId = d.getInt("product_id");
        o.storeId = d.getInt("store_id");
        o.purchaserUserId = d.getInt("purchaser_user_id");
        o.merchantUserId = d.getInt("merchant_user_id");
        o.productName = d.getString("product_name");
        o.productPrice = d.getInt("product_price");
        o.productAmount = d.getInt("product_amount");
        o.productDescription = d.getString("product_description");
        o.productImage = d.getString("product_image");
        o.orderStatus = d.getInt("order_status");
        return o;
    }
}
