package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.vo.OrderVo;

import java.util.Map;

/**
 * Created by Administrator on 2018/6/3 0003.
 */
public interface IOrderService {
    ServerResponse pay(Integer userId, Long orderNo, String path);
    ServerResponse alipayBack(Map<String,String> params);
    ServerResponse queryOrderPayStatus(Integer userId,Long orderNo);
    ServerResponse create(Integer userId,Integer shippingId);
    ServerResponse cancel(Integer userId,Long orderNo);
    ServerResponse getOrderCartProduct(Integer  userId);
    ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo);
    ServerResponse<PageInfo> getOrderList(Integer userId, int pageNum, int pageSize);

    //backend
    ServerResponse<PageInfo> manageList(int pageNum,int pageSize);
    ServerResponse<OrderVo> manageDetail(Long orderNo);
    ServerResponse<PageInfo> manageSearch(Long orderNo,int pageNum,int pageSize);
    ServerResponse<String> manageSendGoods(Long orderNo);
}
