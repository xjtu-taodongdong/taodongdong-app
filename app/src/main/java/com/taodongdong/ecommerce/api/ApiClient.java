package com.taodongdong.ecommerce.api;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class ApiClient extends AbstractApiClient {
    protected Context context;
    protected Handler handler;

    public ApiClient(Context context) {
        this.context = context;
    }

    /**
     * 在当前线程初始化Handler
     */
    public void initHandlerOnCurrentThread() {
        handler = new Handler();
    }

    /**
     * 在主线程（UI线程）种执行Runnable
     * @param task
     */
    public void postRunnable(Runnable task) {
        handler.post(task);
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
                    final String token = d.getString("token");
                    setToken(token);
                    standardOnSuccess(callback, token);
                }

                @Override
                public void onError(int code, String message, Object data) throws JSONException {
                    standardOnError(callback, code, message, data);
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
                standardOnSuccess(callback, msg);
            }

            @Override
            public void onError(int code, String message, Object data) throws JSONException {
                standardOnError(callback, code, message, data);
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
                    standardOnSuccess(callback, (Boolean) data);
                }

                @Override
                public void onError(int code, String message, Object data) throws JSONException {
                    standardOnError(callback, code, message, data);
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
                    standardOnSuccess(callback, (String) data);
                }

                @Override
                public void onError(int code, String message, Object data) throws JSONException {
                    standardOnError(callback, code, message, data);
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
                standardOnSuccess(callback, msg);
            }

            @Override
            public void onError(int code, String message, Object data) throws JSONException {
                standardOnError(callback, code, message, data);
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
                    standardOnSuccess(callback, null);
                } else {
                    JSONObject d = (JSONObject) data;
                    UserInfo u = new UserInfo();
                    u.id = d.getInt("id");
                    u.username = d.getString("username");
                    u.authority = d.getInt("authority");
                    u.balance = d.getInt("balance");
                    standardOnSuccess(callback, u);
                }
            }

            @Override
            public void onError(int code, String message, Object data) throws JSONException {
                standardOnError(callback, code, message, data);
            }
        });
    }

    /**
     * 给当前账户充值
     * 错误列表 NOT_LOGIN
     * @param amount 充值量（可以是负数）
     * @param callback 回调参数为当前用户信息或者null
     */
    public void recharge(int amount, final ApiCallback<UserInfo> callback) {
        try {
            JSONObject input = new JSONObject();
            input.put("amount", amount);
            sendRequest("User.recharge", input, new ApiCallback<Object>() {
                @Override
                public void onSuccess(Object data) throws JSONException {
                    if (data == null) {
                        standardOnSuccess(callback, null);
                    } else {
                        JSONObject d = (JSONObject) data;
                        UserInfo u = new UserInfo();
                        u.id = d.getInt("id");
                        u.username = d.getString("username");
                        u.authority = d.getInt("authority");
                        u.balance = d.getInt("balance");
                        standardOnSuccess(callback, u);
                    }
                }

                @Override
                public void onError(int code, String message, Object data) throws JSONException {
                    standardOnError(callback, code, message, data);
                }
            });
        } catch (JSONException e) {
            onRequestJSONException(e);
        }
    }

    // /**
    //  * 创建商铺
    //  * 错误列表 NOT_LOGIN ALREADY_HAVE_STORE
    //  * @param storeName 商铺名称
    //  * @param callback 回调参数固定为“创建商铺成功”
    //  */
    // public void setUpStore(String storeName, final ApiCallback<String> callback) {
    //     try {
    //         JSONObject input = new JSONObject();
    //         input.put("store_name", storeName);
    //         sendRequest("Store.setUpStore", input, new ApiCallback<Object>() {
    //             @Override
    //             public void onSuccess(Object data) throws JSONException {
    //                 standardOnSuccess(callback, (String) data);
    //             }

    //             @Override
    //             public void onError(int code, String message, Object data) throws JSONException {
    //                 standardOnError(callback, code, message, data);
    //             }
    //         });
    //     } catch (JSONException e) {
    //         onRequestJSONException(e);
    //     }
    // }

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
                standardOnSuccess(callback, s);
            }

            @Override
            public void onError(int code, String message, Object data) throws JSONException {
                standardOnError(callback, code, message, data);
            }
        });
    }

    /**
     * 获取某个商铺下的所有商品（分页）
     * @param storeId 商铺ID
     * @param page  第几页
     * @param perPage 每页多少元素
     * @param callback 回调参数为这一页所有的商品信息
     */
    public void getAllProducts(int storeId, int page, int perPage,
                               final ApiCallback<Page<ProductInfo>> callback) {
        try {
            JSONObject input = new JSONObject();
            input.put("store_id", storeId);
            input.put("page", page);
            input.put("count", perPage);
            sendRequest("Store.getAllProducts", input, new ApiCallback<Object>() {
                @Override
                public void onSuccess(Object data) throws JSONException {
                    JSONObject d = (JSONObject) data;
                    Page r = new Page<ProductInfo>();
                    int n = 0;
                    r.total = d.getInt("total");
                    r.perPage = d.getInt("per_page");
                    r.currentPage = d.getInt("current_page");
                    r.lastPage = d.getInt("last_page");
                    JSONArray arr = d.getJSONArray("data");
                    n = arr.length();
                    ProductInfo[] pl = new ProductInfo[n];
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
                    r.data = pl;
                    standardOnSuccess(callback, r);
                }

                @Override
                public void onError(int code, String message, Object data) throws JSONException {
                    standardOnError(callback, code, message, data);
                }
            });
        } catch (JSONException e) {
            onRequestJSONException(e);
        }
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
                    standardOnSuccess(callback, p);
                }

                @Override
                public void onError(int code, String message, Object data) throws JSONException {
                    standardOnError(callback, code, message, data);
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
                    standardOnSuccess(callback, p);
                }

                @Override
                public void onError(int code, String message, Object data) throws JSONException {
                    standardOnError(callback, code, message, data);
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
                    r.perPage = d.getInt("per_page");
                    r.currentPage = d.getInt("current_page");
                    r.lastPage = d.getInt("last_page");
                    JSONArray arr = d.getJSONArray("data");
                    n = arr.length();
                    ProductInfo[] pl = new ProductInfo[n];
                    for (int i = 0; i < n; i++) {
                        ProductInfo p = new ProductInfo();
                        JSONObject dp = arr.getJSONObject(i);
                        p.id = dp.getInt("id");
                        p.productName = dp.getString("product_name");
                        p.productPrice = dp.getInt("product_price");
                        p.productAmount = dp.getInt("product_amount");
                        p.productDescription = dp.getString("product_description");
                        p.productImage = dp.getString("product_image");
                        pl[i] = p;
                    }
                    r.data = pl;
                    standardOnSuccess(callback, r);
                }

                @Override
                public void onError(int code, String message, Object data) throws JSONException {
                    standardOnError(callback, code, message, data);
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
                    standardOnSuccess(callback, p);
                }

                @Override
                public void onError(int code, String message, Object data) throws JSONException {
                    standardOnError(callback, code, message, data);
                }
            });
        } catch (JSONException e) {
            onRequestJSONException(e);
        }
    }

    /**
     * 上传商品图片（会同步保存至数据库）
     * 错误列表 NO_SUCH_PRODUCT NOT_OWNER_MERCHANT NO_INPUT_FILE NOT_IMAGE
     * @param productId 商品ID
     * @param image 图片文件
     * @param callback 回调参数是该图片的网络地址
     */
    public void uploadImage(int productId, File image, final ApiCallback<String> callback) {
        try {
            JSONObject extra = new JSONObject();
            extra.put("id", productId);
            sendRequest("Product.uploadImage", image, extra, new ApiCallback<Object>() {
                @Override
                public void onSuccess(Object data) throws JSONException {
                    JSONObject d = (JSONObject) data;
                    String url = d.getString("url");
                    standardOnSuccess(callback, url);
                }

                @Override
                public void onError(int code, String message, Object data) throws JSONException {
                    standardOnError(callback, code, message, data);
                }
            });
        } catch (JSONException e) {
            onRequestJSONException(e);
        }
    }

    /**
     * 下架商品
     * 错误列表 NO_SUCH_PRODUCT NOT_OWNER_MERCHANT
     * @param productId 商品ID
     * @param callback 回调参数固定为“下架成功”
     */
    public void removeProduct(int productId, final ApiCallback<String> callback) {
        try {
            JSONObject extra = new JSONObject();
            extra.put("id", productId);
            sendRequest("Product.removeProduct", extra, new ApiCallback<Object>() {
                @Override
                public void onSuccess(Object data) throws JSONException {
                    standardOnSuccess(callback, (String)data);
                }

                @Override
                public void onError(int code, String message, Object data) throws JSONException {
                    standardOnError(callback, code, message, data);
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
                    OrderInfo o = OrderInfo.fromJSONObject(d);
                    standardOnSuccess(callback, o);
                }

                @Override
                public void onError(int code, String message, Object data) throws JSONException {
                    standardOnError(callback, code, message, data);
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
                    standardOnSuccess(callback, (String) data);
                }

                @Override
                public void onError(int code, String message, Object data) throws JSONException {
                    standardOnError(callback, code, message, data);
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
                    standardOnSuccess(callback, (String) data);
                }

                @Override
                public void onError(int code, String message, Object data) throws JSONException {
                    standardOnError(callback, code, message, data);
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
                    standardOnSuccess(callback, (String) data);
                }

                @Override
                public void onError(int code, String message, Object data) throws JSONException {
                    standardOnError(callback, code, message, data);
                }
            });
        } catch (JSONException e) {
            onRequestJSONException(e);
        }
    }

    /**
     * 获取商家的订单列表
     * 错误列表 NOT_LOGIN NO_STORE
     * @param page  第几页
     * @param perPage 每页多少元素
     * @param callback 回调参数为这一页所有的订单信息
     */
    public void getMerchantOrders(int page, int perPage,
                               final ApiCallback<Page<OrderInfo>> callback) {
        try {
            JSONObject input = new JSONObject();
            input.put("page", page);
            input.put("count", perPage);
            sendRequest("Store.getMerchantOrders", input, new ApiCallback<Object>() {
                @Override
                public void onSuccess(Object data) throws JSONException {
                    JSONObject d = (JSONObject) data;
                    Page r = new Page<ProductInfo>();
                    int n = 0;
                    r.total = d.getInt("total");
                    r.perPage = d.getInt("per_page");
                    r.currentPage = d.getInt("current_page");
                    r.lastPage = d.getInt("last_page");
                    JSONArray arr = d.getJSONArray("data");
                    n = arr.length();
                    OrderInfo[] ol = new OrderInfo[n];
                    for (int i = 0; i < n; i++) {
                        JSONObject dp = arr.getJSONObject(i);
                        OrderInfo o = OrderInfo.fromJSONObject(dp);
                        ol[i] = o;
                    }
                    r.data = ol;
                    standardOnSuccess(callback, r);
                }

                @Override
                public void onError(int code, String message, Object data) throws JSONException {
                    standardOnError(callback, code, message, data);
                }
            });
        } catch (JSONException e) {
            onRequestJSONException(e);
        }
    }

    /**
     * 获取客户的订单列表
     * 错误列表 NOT_LOGIN
     * @param page  第几页
     * @param perPage 每页多少元素
     * @param callback 回调参数为这一页所有的订单信息
     */
    public void getPurchaserOrders(int page, int perPage,
                                  final ApiCallback<Page<OrderInfo>> callback) {
        try {
            JSONObject input = new JSONObject();
            input.put("page", page);
            input.put("count", perPage);
            sendRequest("Store.getPurchaserOrders", input, new ApiCallback<Object>() {
                @Override
                public void onSuccess(Object data) throws JSONException {
                    JSONObject d = (JSONObject) data;
                    Page r = new Page<ProductInfo>();
                    int n = 0;
                    r.total = d.getInt("total");
                    r.perPage = d.getInt("per_page");
                    r.currentPage = d.getInt("current_page");
                    r.lastPage = d.getInt("last_page");
                    JSONArray arr = d.getJSONArray("data");
                    n = arr.length();
                    OrderInfo[] ol = new OrderInfo[n];
                    for (int i = 0; i < n; i++) {
                        JSONObject dp = arr.getJSONObject(i);
                        OrderInfo o = OrderInfo.fromJSONObject(dp);
                        ol[i] = o;
                    }
                    r.data = ol;
                    standardOnSuccess(callback, r);
                }

                @Override
                public void onError(int code, String message, Object data) throws JSONException {
                    standardOnError(callback, code, message, data);
                }
            });
        } catch (JSONException e) {
            onRequestJSONException(e);
        }
    }

    public void showToast(final String message) {
        postRunnable(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 调用callback的onSuccess
     * @param callback
     * @param data
     * @param <T>
     */
    protected <T extends Object> void standardOnSuccess(final ApiCallback<T> callback, final T data) {
        postRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.onSuccess(data);
                } catch (JSONException e) {
                    onHandleCallbackJSONException(e);
                }
            }
        });
    }

    /**
     * 调用callback的onError
     * @param callback
     * @param code
     * @param message
     * @param data
     */
    protected void standardOnError(final ApiCallback<? extends Object> callback, final int code, final String message, final Object data) {
        postRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                     callback.onError(code, message, data);
                } catch (JSONException e) {
                    onHandleCallbackJSONException(e);
                }
            }
        });
    }

    @Override
    public void onIOException(IOException e) {
        showToast("网络异常：" + e.getMessage());
    }

    @Override
    public void onRequestJSONException(JSONException e) {
        showToast("请求体JSON异常" + e.getMessage());
    }

    @Override
    public void onResponseJSONException(JSONException e) {
        showToast("响应体JSON异常" + e.getMessage());
    }

    @Override
    public void onResponseFormatInvalid(JSONObject response, JSONException e) {
        e.printStackTrace();
        showToast("响应体格式化JSON异常：" + e.getMessage());
        e.printStackTrace();
    }

    public void onHandleCallbackJSONException(JSONException e) {
        showToast("回调JSON异常" + e.getMessage());
    }
}
