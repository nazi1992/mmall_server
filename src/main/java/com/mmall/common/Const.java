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
    }
    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc","price_asc");
    }
}
