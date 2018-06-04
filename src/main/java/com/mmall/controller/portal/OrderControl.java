package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2018/6/3 0003.
 */
@Controller
@RequestMapping("/order/")
public class OrderControl {

    private Logger logger = LoggerFactory.getLogger(OrderControl.class);
    @Autowired
    private IOrderService iOrderService;

    @RequestMapping("pay.do")
    @ResponseBody

    public ServerResponse pay(HttpSession session, Long orderNo, HttpServletRequest request) {
        /**
         * 生成订单二维码
         */
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createBySuccessMessage("用户未登录，无法获取当前信息");
        }
        String path = request.getSession().getServletContext().getRealPath("upload");
        return iOrderService.pay(currentUser.getId(), orderNo, path);
    }

    public Object alipayCallback(HttpServletRequest request)//只接受一个request，因为支付宝吧所有的数据全部放在了http中
    {
        Map map = request.getParameterMap();
        Map localMap = Maps.newHashMap();
        Iterator iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String name = (String) iterator.next();
            String[] valueStr = (String[]) map.get(name);
            String valuest = "";
            for (int i = 0, len = valueStr.length; i < len; i++) {
                valuest = (i == len - 1) ? valuest + valueStr[i] : valuest + valueStr[i] + ",";//最后得到的是一个以都逗号分割的字符串
            }
            localMap.put(name, valuest);
        }
        logger.info("支付宝回调：sign:{},trade_status:{} 参数：{}", localMap.get("sign"), localMap.get("trade_status"), localMap.toString());

        //验证回调的正确性，和确定gai该回调是否为支付宝发起，以及避免重复通知

        localMap.remove("sign_type");
        try {
            boolean alipayRAS2CheckV2 = AlipaySignature.rsaCheckV2(localMap, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());

            if(!alipayRAS2CheckV2)
            {
                return ServerResponse.createByError("非法请求，验证不通过");
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝验证回调异常",e);
            e.printStackTrace();
        }


        //todo　还需要验证各种数据

        ServerResponse serverResponse = iOrderService.alipayBack(localMap);
        if(serverResponse.isSuccess())
        {
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }

    public ServerResponse<Boolean> queryOrderPayStatus(HttpSession session, Long orderNo)
    {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createBySuccessMessage("用户未登录，无法获取当前信息");
        }
        ServerResponse serverResponse = iOrderService.queryOrderPayStatus(currentUser.getId(),orderNo);
        if(serverResponse.isSuccess())
        {
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }
}