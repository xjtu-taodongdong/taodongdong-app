package com.taodongdong.ecommerce.api;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ApiClient extends AbstractApiClient {
    protected  Context context;

    public ApiClient(Context context) {
        this.context = context;
    }

    /**
     * 登录
     * 错误列表 NO_SUCH_USER PASSWORD_ERROR CANT_CREATE_HASH
     * @param username 用户名
     * @param password 密码
     * @param callback 回调参数为登录token
     */
    public void login(String username, String password, final ApiCallback<String> callback) {
        try {
            JSONObject input = new JSONObject();
            input.put("username", username);
            input.put("password", password);
            sendRequest("User.login", input, new ApiCallback<Object>() {
                @Override
                public void onSuccess(Object data) throws JSONException {
                    JSONObject d = (JSONObject) data;
                    String token = d.getString("token");
                    setToken(token);
                    callback.onSuccess(token);
                }

                @Override
                public void onError(int code, String message, Object data) throws JSONException {
                    callback.onError(code, message, data);
                }
            });
        } catch (JSONException e) {
            onRequestJSONException(e);
        }
    }

    /**
     * 登出
     * @param callback 回调参数为“登出成功”或“您未登录”
     */
    public void logout(final ApiCallback<String> callback) {
        sendRequest("User.logout", null, new ApiCallback<Object>() {
            @Override
            public void onSuccess(Object data) throws JSONException {
                String msg = (String) data;
                setToken(null);
                callback.onSuccess(msg);
            }

            @Override
            public void onError(int code, String message, Object data) throws JSONException {
                callback.onError(code, message, data);
            }
        });
    }

    /**
     * 判断某个用户名是否被注册过
     * @param username 用户名
     * @param callback 回调参数为true或false
     */
    public void isRegistered(String username, final ApiCallback<Boolean> callback) {
        try {
            JSONObject input = new JSONObject();
            input.put("username", username);
            sendRequest("User.isRegistered", input, new ApiCallback<Object>() {
                @Override
                public void onSuccess(Object data) throws JSONException {
                    callback.onSuccess((Boolean) data);
                }

                @Override
                public void onError(int code, String message, Object data) throws JSONException {
                    callback.onError(code, message, data);
                }
            });
        } catch (JSONException e) {
            onRequestJSONException(e);
        }
    }

    /**
     * 注册
     * 错误列表 ALREADY_LOGIN ALREADY_REGISTERED AUTHORITY_ERROR
     * @param username 用户名
     * @param password 密码
     * @param authority 权限等级 0=买家 1=卖家
     * @param callback 回调参数固定为“注册成功”
     */
    public void register(String username, String password, int authority, final ApiCallback<String> callback) {
        try {
            JSONObject input = new JSONObject();
            input.put("username", username);
            input.put("password", password);
            input.put("authority", authority);
            sendRequest("User.register", input, new ApiCallback<Object>() {
                @Override
                public void onSuccess(Object data) throws JSONException {
                    callback.onSuccess((String) data);
                }

                @Override
                public void onError(int code, String message, Object data) throws JSONException {
                    callback.onError(code, message, data);
                }
            });
        } catch (JSONException e) {
            onRequestJSONException(e);
        }
    }

    /**
     * 注册为商家
     * 错误列表 NOT_LOGIN ALREADY_MERCHANT INVALID_AUTHORITY_TO_MERCHANT
     * @param callback 回调参数固定为“注册为商家成功”
     */
    public void approveMerchant(final ApiCallback<String> callback) {
        sendRequest("User.approveMerchant", null, new ApiCallback<Object>() {
            @Override
            public void onSuccess(Object data) throws JSONException {
                String msg = (String) data;
                callback.onSuccess(msg);
            }

            @Override
            public void onError(int code, String message, Object data) throws JSONException {
                callback.onError(code, message, data);
            }
        });
    }

    /**
     * 获取当前登录用户的信息
     * @param callback 回调参数为当前用户信息或者null
     */
    public void getUserInfo(final ApiCallback<UserInfo> callback) {
        sendRequest("User.getUserInfo", null, new ApiCallback<Object>() {
            @Override
            public void onSuccess(Object data) throws JSONException {
                if (data == null) {
                    callback.onSuccess(null);
                } else {
                    JSONObject d = (JSONObject) data;
                    UserInfo u = new UserInfo();
                    u.id = d.getInt("id");
                    u.username = d.getString("username");
                    u.authority = d.getInt("authority");
                    u.balance = d.getInt("balance");
                    callback.onSuccess(u);
                }
            }

            @Override
            public void onError(int code, String message, Object data) throws JSONException {
                callback.onError(code, message, data);
            }
        });
    }

    /**
     * 创建商铺
     * 错误列表 NOT_LOGIN ALREADY_HAVE_STORE
     * @param storeName 商铺名称
     * @param callback 回调参数固定为“创建商铺成功”
     */
    public void setUpStore(String storeName, final ApiCallback<String> callback) {
        try {
            JSONObject input = new JSONObject();
            input.put("store_name", storeName);
            sendRequest("Store.setUpStore", input, new ApiCallback<Object>() {
                @Override
                public void onSuccess(Object data) throws JSONException {
                    callback.onSuccess((String) data);
                }

                @Override
                public void onError(int code, String message, Object data) throws JSONException {
                    callback.onError(code, message, data);
                }
            });
        } catch (JSONException e) {
            onRequestJSONException(e);
        }
    }

    /**
     * 获取我的商铺信息
     * 错误列表 NOT_LOGIN NOT_MERCHANT NO_STORE
     * @param callback 回调参数为商铺信息
     */
    public void getMyStoreInfo(final ApiCallback<StoreInfo> callback) {
        sendRequest("Store.getMyStoreInfo", null, new ApiCallback<Object>() {
            @Override
            public void onSuccess(Object data) throws JSONException {
                JSONObject d = (JSONObject) data;
                StoreInfo s = new StoreInfo();
                s.id = d.getInt("id");
                s.merchantUserId = d.getInt("merchant_user_id");
                s.storeName = d.getString("store_name");
                callback.onSuccess(s);
            }

            @Override
            public void onError(int code, String message, Object data) throws JSONException {
                callback.onError(code, message, data);
            }
        });
    }

    /**
     * 创建新商品
     * 错误列表 NO_STORE
     * @param productName 商品名
     * @param productPrice 商品价格
     * @param productAmount 商品个数
     * @param productDescription 商品描述
     * @param callback 回调参数为新创建的商品全部信息
     */
    public void createProduct(String productName, int productPrice, int productAmount,
                              String productDescription, final ApiCallback<ProductInfo> callback) {
        try {
            JSONObject input = new JSONObject();
            input.put("product_name", productName);
            input.put("product_price", productPrice);
            input.put("product_amount", productAmount);
            input.put("product_description", productDescription);
            sendRequest("Product.createProduct", input, new ApiCallback<Object>() {
                @Override
                public void onSuccess(Object data) throws JSONException {
                    JSONObject d = (JSONObject) data;
                    ProductInfo p = new ProductInfo();
                    p.id = d.getInt("id");
                    p.productName = d.getString("product_name");
                    p.productPrice = d.getInt("product_price");
                    p.productAmount = d.getInt("product_amount");
                    p.productDescription = d.getString("product_description");
                    callback.onSuccess(p);
                }

                @Override
                public void onError(int code, String message, Object data) throws JSONException {
                    callback.onError(code, message, data);
                }
            });
        } catch (JSONException e) {
            onRequestJSONException(e);
        }
    }

    /**
     * 获取商品详情
     * @param id 商品ID
     * @param callback 回调参数为商品全部信息
     */
    public void getProductInfo(int id, final ApiCallback<ProductInfo> callback) {
        try {
            JSONObject input = new JSONObject();
            input.put("id", id);
            sendRequest("Product.getProductInfo", input, new ApiCallback<Object>() {
                @Override
                public void onSuccess(Object data) throws JSONException {
                    JSONObject d = (JSONObject) data;
                    ProductInfo p = new ProductInfo();
                    p.id = d.getInt("id");
                    p.productName = d.getString("product_name");
                    p.productPrice = d.getInt("product_price");
                    p.productAmount = d.getInt("product_amount");
                    p.productDescription = d.getString("product_description");
                    callback.onSuccess(p);
                }

                @Override
                public void onError(int code, String message, Object data) throws JSONException {
                    callback.onError(code, message, data);
                }
            });
        } catch (JSONException e) {
            onRequestJSONException(e);
        }
    }

    /**
     * 根据关键字搜索商品
     * @param keywords 关键字
     * @param page 页码（从1开始）
     * @param perPage 每页个数
     * @param callback 回调参数为指定页码的所有商品和分页信息
     */
    public void searchProducts(String keywords, int page, int perPage,
                               final ApiCallback<Page<ProductInfo>> callback) {
        try {
            JSONObject input = new JSONObject();
            input.put("keywords", keywords);
            input.put("page", page);
            input.put("count", perPage);
            sendRequest("Product.searchProducts", input, new ApiCallback<Object>() {
                @Override
                public void onSuccess(Object data) throws JSONException {
                    JSONObject d = (JSONObject) data;
                    Page r = new Page<ProductInfo>();
                    int n = 0;
                    r.total = d.getInt("total");
                    r.perPage = n = d.getInt("per_page");
                    r.currentPage = d.getInt("current_page");
                    r.lastPage = d.getInt("last_page");
                    ProductInfo[] pl = new ProductInfo[n];
                    JSONArray arr = d.getJSONArray("data");
                    for (int i = 0; i < n; i++) {
                        ProductInfo p = new ProductInfo();
                        JSONObject dp = arr.getJSONObject(i);
                        p.id = dp.getInt("id");
                        p.productName = dp.getString("product_name");
                        p.productPrice = dp.getInt("product_price");
                        p.productAmount = dp.getInt("product_amount");
                        p.productDescription = dp.getString("product_description");
                        pl[i] = p;
                    }
                    callback.onSuccess(r);
                }

                @Override
                public void onError(int code, String message, Object data) throws JSONException {
                    callback.onError(code, message, data);
                }
            });
        } catch (JSONException e) {
            onRequestJSONException(e);
        }
    }

    /**
     * 更新商品详情
     * 错误列表 NO_STORE NO_SUCH_PRODUCT NOT_OWNER_MERCHANT
     * @param productInfo 商品详情 ID为必选参数
     * @param callback 回调参数为更新后的商品详情
     */
    public void modifyProduct(ProductInfo productInfo, final ApiCallback<ProductInfo> callback) {
        try {
            JSONObject input = new JSONObject();
            input.put("id", productInfo.id);
            input.put("product_name", productInfo.productName);
            input.put("product_price", productInfo.productPrice);
            input.put("product_amount", productInfo.productAmount);
            input.put("product_description", productInfo.productDescription);
            sendRequest("Product.modifyProduct", input, new ApiCallback<Object>() {
                @Override
                public void onSuccess(Object data) throws JSONException {
                    JSONObject d = (JSONObject) data;
                    ProductInfo p = new ProductInfo();
                    p.id = d.getInt("id");
                    p.productName = d.getString("product_name");
                    p.productPrice = d.getInt("product_price");
                    p.productAmount = d.getInt("product_amount");
                    p.productDescription = d.getString("product_description");
                    callback.onSuccess(p);
                }

                @Override
                public void onError(int code, String message, Object data) throws JSONException {
                    callback.onError(code, message, data);
                }
            });
        } catch (JSONException e) {
            onRequestJSONException(e);
        }
    }

    /**
     * 创建订单
     * 错误列表 NOT_LOGIN NO_SUCH_PRODUCT NO_SUCH_STORE
     * @param productId 商品ID
     * @param callback 返回订单的全部信息
     */
    public void createOrder(int productId, final ApiCallback<OrderInfo> callback) {
        try {
            JSONObject input = new JSONObject();
            input.put("product_id", productId);
            sendRequest("Order.createOrder", input, new ApiCallback<Object>() {
                @Override
                public void onSuccess(Object data) throws JSONException {
                    JSONObject d = (JSONObject) data;
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
                    callback.onSuccess(o);
                }

                @Override
                public void onError(int code, String message, Object data) throws JSONException {
                    callback.onError(code, message, data);
                }
            });
        } catch (JSONException e) {
            onRequestJSONException(e);
        }
    }

    /**
     * 支付订单
     * 错误列表 NOT_LOGIN NO_SUCH_ORDER INVALID_STATUS NO_ENOUGH_MONEY
     * @param orderId 订单ID
     * @param callback 回调参数固定为“支付成功”
     */
    public void payOrder(int orderId, final ApiCallback<String> callback) {
        try {
            JSONObject input = new JSONObject();
            input.put("order_id", orderId);
            sendRequest("Store.payOrder", input, new ApiCallback<Object>() {
                @Override
                public void onSuccess(Object data) throws JSONException {
                    callback.onSuccess((String) data);
                }

                @Override
                public void onError(int code, String message, Object data) throws JSONException {
                    callback.onError(code, message, data);
                }
            });
        } catch (JSONException e) {
            onRequestJSONException(e);
        }
    }

    /**
     * 商家发货
     * 错误列表 NOT_LOGIN NO_STORE NO_SUCH_ORDER NOT_OWNER_MERCHANT INVALID_STATUS
     * @param orderId 订单ID
     * @param callback 回调参数固定为“发货成功”
     */
    public void sendOrder(int orderId, final ApiCallback<String> callback) {
        try {
            JSONObject input = new JSONObject();
            input.put("order_id", orderId);
            sendRequest("Store.sendOrder", input, new ApiCallback<Object>() {
                @Override
                public void onSuccess(Object data) throws JSONException {
                    callback.onSuccess((String) data);
                }

                @Override
                public void onError(int code, String message, Object data) throws JSONException {
                    callback.onError(code, message, data);
                }
            });
        } catch (JSONException e) {
            onRequestJSONException(e);
        }
    }

    /**
     * 买家确认
     * 错误列表 NOT_LOGIN NO_SUCH_ORDER NOT_OWNER_PURCHASER NO_SUCH_STORE INVALID_STATUS
     * @param orderId 订单ID
     * @param callback 回调参数固定为“确认成功”
     */
    public void confirmOrder(int orderId, final ApiCallback<String> callback) {
        try {
            JSONObject input = new JSONObject();
            input.put("order_id", orderId);
            sendRequest("Store.confirmOrder", input, new ApiCallback<Object>() {
                @Override
                public void onSuccess(Object data) throws JSONException {
                    callback.onSuccess((String) data);
                }

                @Override
                public void onError(int code, String message, Object data) throws JSONException {
                    callback.onError(code, message, data);
                }
            });
        } catch (JSONException e) {
            onRequestJSONException(e);
        }
    }

    @Override
    public void onIOException(IOException e) {
        Toast.makeText(context, "网络异常：" + e.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestJSONException(JSONException e) {
        Toast.makeText(context, "请求体JSON异常：" + e.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponseJSONException(JSONException e) {
        Toast.makeText(context, "响应体JSON异常：" + e.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponseFormatInvalid(JSONObject response, JSONException e) {
        Toast.makeText(context, "响应体JSON格式不正确：" + e.getMessage(), Toast.LENGTH_LONG).show();
    }
}
