package com.mmall.service;

import com.mmall.common.ServerResponse;

import java.util.Map;

/**
 * Created by Administrator on 2018/6/3 0003.
 */
public interface IOrderService {
    ServerResponse pay(Integer userId, Long orderNo, String path);
    ServerResponse alipayBack(Map<String,String> params);
    ServerResponse queryOrderPayStatus(Integer userId,Long orderNo);
}
