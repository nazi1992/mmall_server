package com.mmall.dao;

import com.mmall.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 购物车map
 */
public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectCartByUserIdAndProductId(@Param("userId") Integer userId,@Param("productId") Integer  productId);
    List<Cart> selectCartByUserId(Integer userId);

    int checkProductStatusByUserId(Integer userId);

    int deleteByUserIdProductId(Integer userId,String productIds);

    int updateCheckByUserId(@Param("userId")Integer userId,@Param("productId")Integer productId,@Param("checked")Integer checked);

    int selectCartProductCount(Integer userId);
}