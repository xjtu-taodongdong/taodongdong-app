package com.taodongdong.ecommerce.api;

import org.json.JSONException;

public interface ApiCallback<T> {
    /**
     * data可能为空
     * @param data 响应体数据
     */
    void onSuccess(T data) throws JSONException;

    /**
     * message和data可能为空
     * @param code 错误码
     * @param message 错误消息
     * @param data 响应体数据
     */
    void onError(int code, String message, Object data) throws JSONException;
}
