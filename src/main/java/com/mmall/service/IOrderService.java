package com.mmall.service;

import com.mmall.common.ServerResponse;

/**
 * Created by Administrator on 2018/6/3 0003.
 */
public interface IOrderService {
    ServerResponse pay(Integer userId, Long orderNo, String path);
}
