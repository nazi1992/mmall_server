package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

/**
 * Created by Administrator on 2018/4/9 0009.
 */
public interface IProductService {
    ServerResponse updateOrSaveProduct(Product product);
    ServerResponse setProductStatus(Integer productId,Integer status);
    ServerResponse<ProductDetailVo> getDetail(Integer productId);
    ServerResponse<PageInfo> getProductList(Integer pageNum, Integer pageSize);
    ServerResponse<PageInfo> searchProductList(String productName,Integer productId,Integer pageNum, Integer pageSize);
    ServerResponse<ProductDetailVo> getPruductDetail(Integer productId);
    ServerResponse<PageInfo> getProductByKeywordCategory(String orderBy,String keyword,Integer categoryId,int pageNum,int pageSize);
}
