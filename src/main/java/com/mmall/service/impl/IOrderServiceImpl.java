package com.mmall.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.*;
import com.mmall.pojo.*;
import com.mmall.service.IOrderService;
import com.mmall.util.BigdelcimalUtil;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.FtpUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.OrderItemVo;
import com.mmall.vo.OrderVo;
import com.mmall.vo.ShippingVo;
import com.mmall.vo.orderProductVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Administrator on 2018/6/3 0003.
 */
@Service("iOrderService")
public class IOrderServiceImpl implements IOrderService {
        private Logger log = LoggerFactory.getLogger(IOrderServiceImpl.class);
        @Autowired
        private OrderMapper orderMapper;
        @Autowired
        private OrderItemMapper orderItemMapper;
        @Autowired
        private PayInfoMapper payInfoMapper;

        @Autowired
        private CartMapper cartMapper;
        @Autowired
        private ProductMapper productMapper;
        @Autowired
        private ShippingMapper shippingMapper;

        public ServerResponse create(Integer userId,Integer shippingId)
        {
            //从购物车中获取数据
            List<Cart> carts = cartMapper.selectCartsByUserId(userId);
            //
         //   获取这个订单的总价
            ServerResponse serverResponse = getCartOrderItem(userId,carts);
            if(!serverResponse.isSuccess())
            {
                return serverResponse;
            }
            List<OrderItem> orderItemList =(List<OrderItem>) serverResponse.getData();
            if(CollectionUtils.isEmpty(orderItemList))
            {
                return ServerResponse.createByError("购物车为空");
            }
            BigDecimal totalPrice = gettotalPrice(orderItemList);//拿到订单的总价
            //生成订单
            Order order = this.assembleOrder(userId,shippingId,totalPrice);
            if(order==null)
            {
                return ServerResponse.createByError("生成订单错误");
            }

            //批量插入
            orderItemMapper.batchInsert(orderItemList);
            //减少产品库存
            this.reduceProductCounts(orderItemList);
            //清空购物车
            this.clearCart(carts);
            OrderVo orderVo = this.assembleOrderVo(order,orderItemList);
            return ServerResponse.createBySuccess(orderVo);
        }


        private OrderVo assembleOrderVo(Order order,List<OrderItem> orderItems)
        {
            OrderVo orderVo = new OrderVo();
            orderVo.setOrderNo(order.getOrderNo());
            orderVo.setPayment(order.getPayment());
            orderVo.setPaymentType(order.getPaymentType());
            orderVo.setPaymentTypeDesc(Const.PaymentTypeEnum.codeOf(order.getPaymentType()).getDesc());
            orderVo.setPostage(order.getPostage());
            orderVo.setStatus(order.getStatus());
            orderVo.setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getDesc());
            orderVo.setShippingId(order.getShippingId());

            Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
            if(shipping!=null)
            {
                orderVo.setShippingVo(this.assembleShippingVo(shipping));
                orderVo.setRecevierName(shipping.getReceiverName());

            }
            orderVo.setPaymentTime(DateTimeUtil.timeToStr(order.getPaymentTime()));
            orderVo.setSendTime(DateTimeUtil.timeToStr(order.getSendTime()));
            orderVo.setEndTime(DateTimeUtil.timeToStr(order.getEndTime()));
            orderVo.setCreateTime(DateTimeUtil.timeToStr(order.getCreateTime()));
            orderVo.setCloseTime(DateTimeUtil.timeToStr(order.getCloseTime()));


            orderVo.setImagesHost(PropertiesUtil.getPropertie("ftp.server.http.prefix"));


            List<OrderItemVo> orderItemVoList = Lists.newArrayList();

            for(OrderItem orderItem : orderItems){
                OrderItemVo orderItemVo = assembleOrderItemVo(orderItem);
                orderItemVoList.add(orderItemVo);
            }
            orderVo.setOrderItemList(orderItemVoList);
            return orderVo;


        }
    private OrderItemVo assembleOrderItemVo(OrderItem orderItem){
        OrderItemVo orderItemVo = new OrderItemVo();
        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setProductId(orderItem.getProductId());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setProductImage(orderItem.getProductImage());
        orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVo.setQuantity(orderItem.getQuantity());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());

        orderItemVo.setCreateTime(DateTimeUtil.timeToStr(orderItem.getCreateTime()));
        return orderItemVo;
    }

    private ShippingVo assembleShippingVo(Shipping shipping)
    {
        ShippingVo shippingVo = new ShippingVo();
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverProvince(shipping.getReceiverProvince());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverZip(shipping.getReceiverZip());
        shippingVo.setReceiverPhone(shippingVo.getReceiverPhone());
        return shippingVo;

    }
        private void clearCart(List<Cart> carts)
        {
            /**
             * 清空购物车
             */
            for (Cart cart:carts
                 ) {
                cartMapper.deleteByPrimaryKey(cart.getId());
            }
        }
        private void reduceProductCounts(List<OrderItem> orderItems)
        {
            for (OrderItem or :
                    orderItems) {
                Product product = productMapper.selectByPrimaryKey(or.getProductId());
                product.setStock(product.getStock()-or.getQuantity());
                productMapper.updateByPrimaryKeySelective(product);
            }

        }
        private Order assembleOrder(Integer userId,Integer shipping,BigDecimal payment)
        {
            /**
             * 生成订单
             */
            Order order = new Order();
            long orderNo = this.getOrderNo();
            order.setOrderNo(orderNo);
            order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());//状态，未付款
            order.setPostage(0);//运费 0，即包邮
            order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());//支付方式,在线支付
            order.setPayment(payment);
            order.setUserId(userId);
            order.setShippingId(shipping);
            //发货时间，付款时间等等，还未设置
            int rowCount = orderMapper.insert(order);
            if(rowCount>0)
            {
                return order;
            }
            return null;
        }

        private long getOrderNo()
    {
        long currentTime = System.currentTimeMillis();
        return currentTime+new Random().nextInt(100);
    }
        private BigDecimal gettotalPrice(List<OrderItem> lists){  //获取订单总价
            BigDecimal payment = new BigDecimal("0");
            for(int i=0,len = lists.size();i<len;i++)
            {
                payment= BigdelcimalUtil.add(payment.doubleValue(),lists.get(i).getTotalPrice().doubleValue());
            }
            return payment;
        }
        public ServerResponse<List<OrderItem>> getCartOrderItem(Integer userId,List<Cart> carts)
        {
            List<OrderItem> orderItems = Lists.newArrayList();
            if(CollectionUtils.isEmpty(carts))
            {
                return ServerResponse.createByError("购物车为空");
            }
            for(Cart cart:carts)
            {
                OrderItem orderItem = new OrderItem();
               Product product = productMapper.selectByPrimaryKey(cart.getProductId());
                if(ResponseCode.ProductDetail.ONSALE.getCode()!=product.getStatus())
                {
                    return ServerResponse.createByError(product.getName()+"产品不是在线售卖状态");
                }
                if(cart.getQuantity()>product.getStock())
                {
                    return ServerResponse.createByError(product.getName()+"库存不足");

                }
                orderItem.setUserId(userId);
                orderItem.setProductId(product.getId());
                orderItem.setCurrentUnitPrice(product.getPrice());//购买当时的价格
                orderItem.setProductName(product.getName());
                orderItem.setProductImage(product.getMainImage());
                orderItem.setQuantity(cart.getQuantity());
                orderItem.setTotalPrice(BigdelcimalUtil.mul(product.getPrice().doubleValue(),cart.getQuantity().doubleValue()));
                orderItems.add(orderItem);
            }
            return ServerResponse.createBySuccess(orderItems);
        }



        public ServerResponse pay(Integer userId,Long orderNo,String path)
        {
            Map<String,String> map = Maps.newHashMap();
            Order order = orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
            if(order!=null)
            {
                return ServerResponse.createByError("用户没有该订单");
            }

            map.put("orderNo",String.valueOf(order.getOrderNo()));

            // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
            // 需保证商户系统端不能重复，建议通过数据库sequence生成，
            String outTradeNo = order.getOrderNo().toString();

            // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
            String subject = new StringBuilder().append("happymall扫码支付订单号:").append(outTradeNo).toString();

            // (必填) 订单总金额，单位为元，不能超过1亿元
            // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
            String totalAmount = order.getPayment().toString();

            // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
            // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
            String undiscountableAmount = "0";

            // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
            // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
            String sellerId = "";

            // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
            String body = new StringBuilder().append("订单").append(outTradeNo).append("共支付").append(totalAmount).append("元").toString();

            // 商户操作员编号，添加此参数可以为商户操作员做销售统计
            String operatorId = "test_operator_id";

            // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
            String storeId = "test_store_id";

            // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
            ExtendParams extendParams = new ExtendParams();
            extendParams.setSysServiceProviderId("2088100200300400500");

            // 支付超时，定义为120分钟
            String timeoutExpress = "120m";

            // 商品明细列表，需填写购买商品详细信息，
            List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();

            List<OrderItem> orderItems = orderItemMapper.getListByOrderNoAndUserId(orderNo,userId);///查询 订单内的上商品s
            for (OrderItem o:orderItems
                 ) {
                GoodsDetail goods1 = GoodsDetail.newInstance(o.getProductId().toString(), o.getProductName(), BigdelcimalUtil.mul(o.getCurrentUnitPrice().doubleValue(),new Double(100)).longValue(), 1);
                goodsDetailList.add(goods1);
            }
//            // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
//            GoodsDetail goods1 = GoodsDetail.newInstance("goods_id001", "xxx小面包", 1000, 1);
//            // 创建好一个商品后添加至商品明细列表
//            goodsDetailList.add(goods1);
//
//            // 继续创建并添加第一条商品信息，用户购买的产品为“黑人牙刷”，单价为5.00元，购买了两件
//            GoodsDetail goods2 = GoodsDetail.newInstance("goods_id002", "xxx牙刷", 500, 2);
//            goodsDetailList.add(goods2);

            // 创建扫码支付请求builder，设置请求参数
            AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                    .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                    .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                    .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                    .setTimeoutExpress(timeoutExpress)
                                    .setNotifyUrl(PropertiesUtil.getPropertie("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                    .setGoodsDetailList(goodsDetailList);
            /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
             *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
             */
            Configs.init("zfbinfo.properties");

            /** 使用Configs提供的默认参数
             *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
             */
            AlipayTradeService tradeService =  new AlipayTradeServiceImpl.ClientBuilder().build();
            AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
            switch (result.getTradeStatus()) {
                case SUCCESS:
                    log.info("支付宝预下单成功: )");

                    AlipayTradePrecreateResponse response = result.getResponse();
                    dumpResponse(response);
                    File file = new File(path);
                    if(!file.exists())
                    {
                        file.setWritable(true);
                        file.mkdirs();
                    }
                    // 需要修改为运行机器上的路径
                    String qrPath = String.format(path+"/qr-%s.png", response.getOutTradeNo());
                    String qrFileName =  String.format("qr-%s.png", response.getOutTradeNo());
                    ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);//讲二维码文件生产（返回）当前目录下

                    File targetFile = new File(path,qrFileName);
                    try {
                        FtpUtil.uploadFile(Lists.newArrayList(targetFile));
                    } catch (IOException e) {
                        log.error("上传ftp失败");
                        e.printStackTrace();
                    }
                    log.info("qrPath:" + qrPath);

                    String qrUrl = PropertiesUtil.getPropertie("ftp.server.http.prefix")+targetFile.getName();//拿到ftp服务器上的文件路径返回到前端展示
                    map.put("qrUrl",qrUrl);
                    return ServerResponse.createBySuccess(map);
                    //                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);
                   // break;

                case FAILED:
                    log.error("支付宝预下单失败!!!");
                    return ServerResponse.createByError("支付宝预下单失败!!!");

                case UNKNOWN:
                    log.error("系统异常，预下单状态未知!!!");
                    return ServerResponse.createByError("系统异常，预下单状态未知!!!");


                default:
                    log.error("不支持的交易状态，交易返回异常!!!");
                    return ServerResponse.createByError("系统异常，预下单状态未知!!!");

            }
        }
    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {

        if (response != null) {

            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
           log.info("body:" + response.getBody());
        }
    }
    public ServerResponse alipayBack(Map<String,String> params)
    {
        /**
         * 几种交易状态都会进入到回调函数里
         */
        Long orderNo = Long.parseLong(params.get("out_trade_no"));///订单号
        String tradeNo = params.get("trade_no");//交易号
        String trade_status= params.get("trade_status");//交易状态
        Order order = orderMapper.selectByorderNo(orderNo);
        if(order==null)
        {
            return ServerResponse.createByError("非快乐幕商城的订单回调忽略");
        }
        if(order.getStatus()>= Const.OrderStatusEnum.SHIPPED.getCode())
        {
            return ServerResponse.createBySuccess("支付宝重复调用");//返回一个成功的标识，避免前端重复调用接口
        }
        if(Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(trade_status))
        {
            order.setPaymentTime(DateTimeUtil.strToTime(params.get("gmt_payment")));//更新交易时间
            order.setStatus(Const.OrderStatusEnum.PAID.getCode());
            orderMapper.updateByPrimaryKeySelective(order);//更新订单，状态改为已付款
        }
        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());//支付形式
        payInfo.setPlatformNumber(tradeNo);//交易号
        payInfo.setPlatformStatus(trade_status);
        payInfoMapper.insert(payInfo);
        return ServerResponse.createBySuccess();//返回前端回调的结果

    }
    public ServerResponse queryOrderPayStatus(Integer userId,Long orderNo)
    {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        if(order==null)
        {
            return ServerResponse.createByError("用户没有该订单");

        }
        if(order.getStatus()>=Const.OrderStatusEnum.SHIPPED.getCode())
        {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    public ServerResponse cancel(Integer userId,Long orderNo)
    {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        if(order==null)
        {
            return ServerResponse.createByError("用户五次订单");
        }
        if(order.getStatus()!=Const.OrderStatusEnum.NO_PAY.getCode())
        {
            return ServerResponse.createByError("取消失败,已付款无法取消");
        }
        Order updateOrder = new Order();
        updateOrder.setUserId(userId);
        updateOrder.setStatus(Const.OrderStatusEnum.CANCEL.getCode());
        int row = orderMapper.updateByPrimaryKeySelective(updateOrder);
        if(row>0)
        {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError("取消失败");
    }

    public ServerResponse getOrderCartProduct(Integer  userId)
    {
        orderProductVo orderProductVo = new orderProductVo();

        //从购物车中获取数据
        List<Cart> carts = cartMapper.selectCartsByUserId(userId);
        ServerResponse serverResponse = getCartOrderItem(userId,carts);
        if(!serverResponse.isSuccess())
        {
            return serverResponse;
        }
        List<OrderItem> orderItemList =(List<OrderItem>) serverResponse.getData();
        List<OrderItemVo> orderItemVos = Lists.newArrayList();
        BigDecimal payment = new BigDecimal("0");
        for(OrderItem orderItem:orderItemList)
        {
            payment = BigdelcimalUtil.add(payment.doubleValue(),orderItem.getTotalPrice().doubleValue());
            orderItemVos.add(assembleOrderItemVo(orderItem));
        }
        orderProductVo.setOrderItemVoList(orderItemVos);
        orderProductVo.setProductTotalPrice(payment.toString());
        orderProductVo.setImageHost(PropertiesUtil.getPropertie("ftp.server.http.prefix"));
        return ServerResponse.createBySuccess(orderProductVo);

    }
}
