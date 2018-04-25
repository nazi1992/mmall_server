package com.mmall.controller.backend;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created by Administrator on 2018/4/9 0009.
 */
@Controller
@RequestMapping("/manage/product")
public class ProductManagerController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;
    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse ProductSave(HttpSession session, Product product)
    {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user!=null)
        {
            if(iUserService.checkAdminRole(user).isSuccess())
            {
                //执行产品更新或者保存操作
                 return  iProductService.updateOrSaveProduct(product);

            }
            else
            {
                return ServerResponse.createByError("无权限");
            }
        }
        else
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要登录");
        }
    }
    //产品上架
    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId,Integer status)
    {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user!=null)
        {
            if(iUserService.checkAdminRole(user).isSuccess())
            {
                //执行产品更新或者保存操作
                return  iProductService.setProductStatus(productId,status);

            }
            else
            {
                return ServerResponse.createByError("无权限");
            }
        }
        else
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要登录");
        }
    }
    //产品详情
    @RequestMapping("getDetail.do")
    @ResponseBody
    public ServerResponse getDetail(HttpSession session, Integer productId)
    {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user!=null)
        {
            if(iUserService.checkAdminRole(user).isSuccess())
            {
                //获取产品详情
                return  iProductService.getDetail(productId);

            }
            else
            {
                return ServerResponse.createByError("无权限");
            }
        }
        else
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要登录");
        }
    }
    //产品详情
    @RequestMapping("getlist.do")
    @ResponseBody
    public ServerResponse<PageInfo> getProductlist(HttpSession session, @RequestParam(value="pageNum", defaultValue = "1") Integer  pageNum, @RequestParam(value="pageNum", defaultValue = "1") Integer  pageSize)
    {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user!=null)
        {
            if(iUserService.checkAdminRole(user).isSuccess())
            {
                //获取产品列表
                return  iProductService.getProductList(pageNum,pageSize);

            }
            else
            {
                return ServerResponse.createByError("无权限");
            }
        }
        else
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要登录");
        }
    }
    //根据产品名称或者id查询商品列表
    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> getProductlist(HttpSession session,String productName,Integer productId, @RequestParam(value="pageNum", defaultValue = "1") Integer  pageNum, @RequestParam(value="pageNum", defaultValue = "1") Integer  pageSize)
    {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user!=null)
        {
            if(iUserService.checkAdminRole(user).isSuccess())
            {
                //获取产品列表
                return  iProductService.searchProductList(productName,productId,pageNum,pageSize);

            }
            else
            {
                return ServerResponse.createByError("无权限");
            }
        }
        else
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要登录");
        }
    }
    //上传文件
    @RequestMapping("uplaod.do")
    @ResponseBody
    public ServerResponse upload(HttpSession session,@RequestParam(value = "upload_file",required = false) MultipartFile partFile , HttpServletRequest request)
    {
         User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user!=null)
        {
            if(iUserService.checkAdminRole(user).isSuccess())
            {
                String path = request.getSession().getServletContext().getRealPath("upload");
                String fileName =  iFileService.upload(partFile,path);
                String url = PropertiesUtil.getPropertie("ftp.server.http.prefix") + fileName;
                //将其封装称map格式的数据
                Map map = Maps.newHashMap();
                map.put("uri",fileName);
                map.put("url",url);
                return ServerResponse.createBySuccess(map);

            }
            else
            {
                return ServerResponse.createByError("无权限");
            }
        }
        else
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要登录");
        }
    }
    //富文本的上传
    @RequestMapping("richText_upload.do")
    @ResponseBody
    public Map upload_txt(HttpSession session, @RequestParam(value = "upload_file",required = false) MultipartFile partFile , HttpServletRequest request, HttpServletResponse response)
    {
        Map resultMap = Maps.newHashMap();
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user!=null)
        {
            //富文本的返回值有自己的要求，我们使用的是simditor
            if(iUserService.checkAdminRole(user).isSuccess())
            {
                String path = request.getSession().getServletContext().getRealPath("upload");
                String fileName =  iFileService.upload(partFile,path);
                String url = PropertiesUtil.getPropertie("ftp.server.http.prefix") + fileName;
                if(StringUtils.isBlank(fileName))
                {
                    resultMap.put("success",false);
                    resultMap.put("msg","上传失败");
                    return resultMap;
                }
                response.setHeader("Access-Control-Allow-Headers","X-File-Name");
                resultMap.put("success",false);
                resultMap.put("msg","未登录需要登录");
                resultMap.put("file_path",url);
                return resultMap;

            }
            else
            {
                resultMap.put("success",false);
                resultMap.put("msg","无权限");
                return resultMap;
            }
        }
        else
        {
            resultMap.put("success",false);
            resultMap.put("msg","未登录需要登录");
            return resultMap;
        }
    }
}
