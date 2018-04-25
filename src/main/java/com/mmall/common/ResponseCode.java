package com.mmall.common;

/**
 * Created by Administrator on 2018/3/15 0015.
 * 用枚举定义错误码
 */
public enum  ResponseCode {
    SUCCESS(0,"SUCCESS"),//成功
    ERROR(1,"ERROR"),//错误
    NEED_LOGIN(10,"NEED_LOGIN"),//需要登录,
    ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT");//参数错误
    private final int code;
    private final String desc;
     ResponseCode(int code,String desc)
    {
        this.code = code;
        this.desc = desc;
    }
    public int getCode()
    {
        return code;
    }
    public String getDesc(){
        return desc;
    }

    public enum ProductDetail
    {
        ONSALE(1,"在线");

        private int code;
        private String desc;

        ProductDetail(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }
         public int getCode() {
            return code;
        }


    }
}
