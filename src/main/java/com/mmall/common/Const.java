package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Created by Administrator on 2018/3/15 0015.
 */
public class Const {
    public static final String CURRENT_USER = "current_user";

    //校验用户名与邮箱
    public static final String EMAIL = "email";
    public static final String USERNAME = "username";

    public interface Role//角色的分类
    {
        int ROLE_CUSTOMER = 0;//普通用户
        int ROLE_ADMIN = 1;//管理员
    }
    public interface  Cart{
        int CHECK = 0;//购物车选中状态
        int NOT_CHECK =1;//购物车未选中状态
        String STOCK_LIMIT_SUCCESS = "STOCK_LIMIT_SUCCESS";
        String STOCK_LIMIT_FAIL = "STOCK_LIMIT_FAIL";
    }
    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc","price_asc");
    }
    public enum OrderStatusEnum
    {
        CANCEL(0,"已取消"),
        NO_PAY(20,"未支付"),
        PAID(40,"已付款"),
        SHIPPED(50,"已发货"),
        ORDER_SUCCESS(60,"订单完成"),
        ORDER_CLOSE(1,"订单关闭");


        private int code;
        private String desc;

        OrderStatusEnum(int code, String desc) {
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
