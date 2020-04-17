package com.taodongdong.ecommerce.api;

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
}
