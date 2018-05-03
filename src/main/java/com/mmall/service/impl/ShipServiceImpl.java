package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/5/2 0002.
 */
@Service("shopService")
public class ShipServiceImpl implements ShipService {
    @Autowired
    private ShippingMapper shippingMapper;
    public ServerResponse add(Integer userId, Shipping shipping)
    {
        shipping.setUserId(userId);

        int resultCount = shippingMapper.insert(shipping);
        if(resultCount>0)
        {
            Map map = Maps.newHashMap();
            map.put("shippingId",shipping.getId());
            return ServerResponse.createBySuccess(map);
        }
        return ServerResponse.createByError("增加地址失败");
    }
    public ServerResponse del(Integer userId, Integer shippingId)
    {
        int resultCount = shippingMapper.delByUserIdShippingId(userId,shippingId);
        if(resultCount>0)
        {

            return ServerResponse.createBySuccess("删除成功");
        }
        return ServerResponse.createByError("增加地址失败");
    }
    public ServerResponse update(Integer userId, Shipping shipping)
    {
        shipping.setUserId(userId);

        int resultCount = shippingMapper.insert(shipping);
        if(resultCount>0)
        {
            Map map = Maps.newHashMap();
            map.put("shippingId",shipping.getId());
            return ServerResponse.createBySuccess(map);
        }
        return ServerResponse.createByError("增加地址失败");
    }

    public ServerResponse select(Integer userId,Integer shippingId)
    {
        Shipping shipping = shippingMapper.selectByUserIdShippingId(userId,shippingId);

        if(shipping!=null)
        {
            return ServerResponse.createBySuccess(shipping);
        }
        return ServerResponse.createByError("查询失败");
    }

    public ServerResponse<PageInfo> list(Integer userId,Integer pageNum,Integer pageSize)
    {
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippings = shippingMapper.selectByUserId(userId);
        PageInfo pageInfos = new PageInfo<>(shippings);

        return ServerResponse.createBySuccess(pageInfos);

    }
}
