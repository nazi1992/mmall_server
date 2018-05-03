package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.ShipService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2018/5/2 0002.
 */
@RequestMapping("/shipping/")
public class ShipController {
    private ShipService shipService;

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse add(HttpSession httpSession, Shipping shipping)
    {
        User user = (User)httpSession.getAttribute(Const.CURRENT_USER);
        if(user == null)
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return shipService.add(user.getId(),shipping);
    }
    @RequestMapping("del.do")
    @ResponseBody
    public ServerResponse del(HttpSession httpSession, Integer shippingId)
    {
        User user = (User)httpSession.getAttribute(Const.CURRENT_USER);
        if(user == null)
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return shipService.del(user.getId(),shippingId);
    }
    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse update(HttpSession httpSession, Shipping shipping)
    {
        User user = (User)httpSession.getAttribute(Const.CURRENT_USER);
        if(user == null)
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return shipService.update(user.getId(),shipping);
    }

    //查询地址详情
    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse select(HttpSession httpSession, Integer shippingId)
    {
        User user = (User)httpSession.getAttribute(Const.CURRENT_USER);
        if(user == null)
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return shipService.select(user.getId(),shippingId);
    }
    //查询所有地址
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(HttpSession httpSession,@RequestParam(value="pageNum", defaultValue = "1") Integer  pageNum,
                               @RequestParam(value="pageSize", defaultValue = "10") Integer  pageSize,Integer shippingId)
    {
        User user = (User)httpSession.getAttribute(Const.CURRENT_USER);
        if(user == null)
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return shipService.list(user.getId(),pageNum,pageSize);
    }
}
