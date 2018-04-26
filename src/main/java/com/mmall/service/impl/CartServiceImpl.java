package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.pojo.Cart;
import com.mmall.service.CartService;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2018/4/26 0026.
 */
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    public ServerResponse add(Integer userId,Integer productId,Integer count)
    {
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId,productId);
        if(cart==null)
        {
            Cart cart1 = new Cart();
            cart1.setQuantity(count);
            cart1.setChecked(Const.Cart.CHECK);
            cart1.setUserId(userId);
            cart1.setProductId(productId);
        }
        else
        {
            count = cart.getQuantity()+count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return null;
    }
    private CartVo getCartVolimit(Integer userId)
    {
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        List<CartProductVo> lists = Lists.newArrayList();

        BigDecimal bigDecimal = new BigDecimal("0");
    }
}
