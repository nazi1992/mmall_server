package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.CartService;
import com.mmall.util.BigdelcimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2018/4/26 0026.
 */
@Service("cartService")
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;


    public ServerResponse list(Integer userId)
    {
        CartVo cartVo = this.getCartVolimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    //  全选，非全选
    public ServerResponse select(Integer userId,Integer productId,Integer checked)
    {
         cartMapper.updateCheckByUserId(userId,productId,checked);
         return this.list(userId);
    }

    //查询购物车中产品的数量
    public ServerResponse selectCartProductCount(Integer userId){
        if(userId == null)
        {
            return ServerResponse.createBySuccess(0);
        }
        return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
    }


    public ServerResponse add(Integer userId,Integer productId,Integer count)
    {
        if(productId ==null || count ==null)
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
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
        return this.list(userId);
    }

    public ServerResponse update(Integer userId,Integer productId,Integer count)
    {
        if(productId ==null || count ==null)
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId,productId);
        if(cart!=null)
        {
            cart.setQuantity(count);
        }
        return this.list(userId);
    }

    public  ServerResponse deleteProduct(Integer userId,String productIds){
        List<String> productList = Splitter.on(",").splitToList(productIds);
        if(CollectionUtils.isEmpty(productList))
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());

        }
        cartMapper.deleteByUserIdProductId(userId,productIds);
        return this.list(userId);
    }

    private CartVo getCartVolimit(Integer userId)
    {
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        List<CartProductVo> lists = Lists.newArrayList();

        BigDecimal bigDecimalTotalPrice = new BigDecimal("0");
        for (Cart cartItem:cartList
             ) {
            CartProductVo cartProductVo = new CartProductVo();
            cartProductVo.setUserId(cartItem.getUserId());
            cartProductVo.setProductId(cartItem.getProductId());
            cartProductVo.setQuantity(cartItem.getQuantity());
            cartProductVo.setId(cartItem.getId());
            Product product =productMapper.selectByPrimaryKey(cartItem.getProductId());
            if(product!=null)
            {
                cartProductVo.setProductMainImage(product.getMainImage());
                cartProductVo.setProductSubtitle(product.getSubtitle());
                cartProductVo.setProductName(product.getName());
                cartProductVo.setProductPrice(product.getPrice());
                cartProductVo.setProductStatus(product.getStatus());
                cartProductVo.setProductStock(product.getStock());
                //赋值 产品的一些相关信息，比如产品图片等
                int buylimitCount = 0;
                if(product.getStock()>=cartItem.getQuantity())
                {
                    buylimitCount = cartItem.getQuantity();
                    cartProductVo.setLimitQutity(Const.Cart.STOCK_LIMIT_SUCCESS);
                    //设置限制成功，及购物车的数量少于产品库存是数量
                }
                else
                {
                    cartProductVo.setLimitQutity(Const.Cart.STOCK_LIMIT_FAIL);

                    Cart cart1 = new Cart();
                    cart1.setQuantity(product.getStock());
                    cart1.setId(product.getId());
                    cartMapper.updateByPrimaryKeySelective(cart1);
                    //更新购物车的数量
                }
                cartProductVo.setQuantity(buylimitCount);
                cartProductVo.setProductChecked(cartItem.getChecked());
                cartProductVo.setProductTotalPrice(BigdelcimalUtil.mul(cartItem.getQuantity().doubleValue(),product.getPrice().doubleValue()));
            }
            if(cartItem.getChecked() == Const.Cart.CHECK)
            {
                //如果已经勾选，则增加到整个购物车总价中
                bigDecimalTotalPrice = BigdelcimalUtil.add(bigDecimalTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
            }
            lists.add(cartProductVo);
        }
        cartVo.setCartProductVoList(lists);
        cartVo.setCartTotalPrice(bigDecimalTotalPrice);//设置购物车view
        cartVo.setAllChecked(this.productIsAllCheck(userId));
        cartVo.setImgHost(PropertiesUtil.getPropertie("ftp.server.http.prefix"));
        return cartVo;

    }
    private boolean productIsAllCheck(Integer userId){
        if(userId == null)
        {
            return false;
        }

        return  cartMapper.checkProductStatusByUserId(userId)==0?true:false;
    }


}
