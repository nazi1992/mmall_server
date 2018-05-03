package com.mmall.service;

import com.mmall.common.ServerResponse;

/**
 * Created by Administrator on 2018/4/26 0026.
 */
public interface CartService {
    ServerResponse add(Integer userId, Integer productId, Integer count);
    ServerResponse update(Integer userId,Integer productId,Integer count);
    ServerResponse deleteProduct(Integer userId,String productIds);
    ServerResponse list(Integer userId);
    ServerResponse select(Integer userId,Integer productId,Integer checked);
    ServerResponse selectCartProductCount(Integer userId);
}
