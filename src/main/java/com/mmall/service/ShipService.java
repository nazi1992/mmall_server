package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;

/**
 * Created by Administrator on 2018/5/2 0002.
 */
public interface ShipService {
    ServerResponse add(Integer userId, Shipping shipping);
    ServerResponse del(Integer userId, Integer shippingId);
    ServerResponse update(Integer userId, Shipping shipping);

    ServerResponse<PageInfo> list(Integer userId, Integer pageNum, Integer pageSize);
    ServerResponse select(Integer userId,Integer shippingId);
}
