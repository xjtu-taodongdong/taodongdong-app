package com.taodongdong.ecommerce.api;

public class Page<T> {
    /**
     * 数据总量（个数）
     */
    public int total;
    /**
     * 每页数据个数
     */
    public int perPage;
    /**
     * 当前页码（从1开始）
     */
    public int currentPage;
    /**
     * 最后一页页码（从1开始）
     */
    public int lastPage;
    /**
     * 这一页的数据
     */
    public T[] data;
}
