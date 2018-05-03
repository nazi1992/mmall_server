package com.mmall.controller.portal;

import com.mmall.common.ServerResponse;
import com.mmall.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2018/4/25 0025.
 */
@Controller
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private IProductService iProductService;
    @RequestMapping(value="/detail.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getDetail(Integer productId){
        return iProductService.getPruductDetail(productId);
    }

    //根据关键字查询或者id查询产品
    @RequestMapping(value="/getProductList.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getProductList(  @RequestParam(value="orderBy",required = false) String orderBy,
                                          String keyWord,
                                          Integer categoryId,
                                          @RequestParam(value="pageNum", defaultValue = "1") Integer  pageNum,
                                          @RequestParam(value="pageSize", defaultValue = "10") Integer  pageSize){
        return iProductService.getProductByKeywordCategory(orderBy,keyWord,categoryId,pageNum,pageSize);
    }
}
