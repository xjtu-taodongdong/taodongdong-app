package com.taodongdong.ecommerce.api;

public class Errors {
    public final static int NOT_LOGIN = 1000;

    public final static int NO_SUCH_USER = 10000;
    public final static int PASSWORD_ERROR = 10001;
    public final static int CANT_CREATE_HASH = 10002;
    public final static int ALREADY_LOGIN = 10003;
    public final static int ALREADY_REGISTERED = 10004;
    public final static int AUTHORITY_ERROR = 10005;

    public final static int ALREADY_MERCHANT = 11000;
    public final static int INVALID_AUTHORITY_TO_MERCHANT = 11001;

    public final static int ALREADY_HAVE_STORE = 12000;
    public final static int NOT_MERCHANT = 12001;
    public final static int NO_STORE = 12002;

    public final static int NO_SUCH_PRODUCT = 13000;
    public final static int NO_SUCH_STORE = 13001;
    public final static int NO_SUCH_ORDER = 13002;
    public final static int NOT_OWNER_MERCHANT = 13003;
    public final static int NOT_OWNER_PURCHASER = 13004;
    public final static int INVALID_STATUS = 13005;
    public final static int NO_ENOUGH_MONEY = 13006;
}
