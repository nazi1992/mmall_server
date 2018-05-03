package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.pojo.User;
import com.mmall.service.CartService;
import com.mmall.service.IProductService;
import com.mmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2018/4/26 0026.
 */
@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;


    @RequestMapping(value="/list.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<CartVo> getProductList(HttpSession httpSession, Integer productId, int count){
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null)
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return cartService.list(user.getId());
    }

    //增加到购物车
    @RequestMapping(value="/add.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<CartVo> productAdd(HttpSession httpSession, Integer productId, int count){
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null)
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return cartService.add(user.getId(),productId,count);
    }

    //更新购物车
    @RequestMapping(value="/update.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<CartVo> productUpdate(HttpSession httpSession, Integer productId, int count){
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null)
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return cartService.update(user.getId(),productId,count);
    }

    //删除购物车里面的产品
    @RequestMapping(value="/delete.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<CartVo> productDelte(HttpSession httpSession,String productIds){
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null)
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return cartService.deleteProduct(user.getId(),productIds);
    }

    //全选
    //全反选
    //全选
    @RequestMapping(value="/select_all.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<CartVo> select_all(HttpSession httpSession){
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null)
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return cartService.select(user.getId(),null,Const.Cart.CHECK);
    }
    //全反选
    @RequestMapping(value="/select_all_un.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<CartVo> select_all_un(HttpSession httpSession){
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null)
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return cartService.select(user.getId(),null,Const.Cart.NOT_CHECK);
    }

    //单选
    @RequestMapping(value="/select.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<CartVo> select(HttpSession httpSession,Integer productId){
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null)
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return cartService.select(user.getId(),productId,Const.Cart.CHECK);
    }
    //取消单选
    @RequestMapping(value="/select_un.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<CartVo> select_un(HttpSession httpSession,Integer productId){
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null)
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return cartService.select(user.getId(),productId,Const.Cart.NOT_CHECK);
    }

    //查询购物车里产品的数量
    @RequestMapping(value="/selectCartProductCount.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Integer> selectCartProductCount(HttpSession httpSession){
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null)
        {
            return ServerResponse.createBySuccess(0);
        }
        return cartService.selectCartProductCount(user.getId());
    }
}
