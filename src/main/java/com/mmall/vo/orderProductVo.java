package com.mmall.vo;

import java.util.List;

/**
 * Created by Administrator on 2018/6/10 0010.
 */
public class orderProductVo {
    private List<OrderItemVo> orderItemVoList;
    private String ProductTotalPrice;
    private String imageHost;

    public List<OrderItemVo> getOrderItemVoList() {
        return orderItemVoList;
    }

    public void setOrderItemVoList(List<OrderItemVo> orderItemVoList) {
        this.orderItemVoList = orderItemVoList;
    }

    public String getProductTotalPrice() {
        return ProductTotalPrice;
    }

    public void setProductTotalPrice(String productTotalPrice) {
        ProductTotalPrice = productTotalPrice;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}
