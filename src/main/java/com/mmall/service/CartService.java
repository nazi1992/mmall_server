package com.mmall.service;

import com.mmall.common.ServerResponse;

/**
 * Created by Administrator on 2018/4/26 0026.
 */
public interface CartService {
    ServerResponse add(String userId, Integer productId, Integer count);
}
