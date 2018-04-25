package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by Administrator on 2018/4/4 0004.
 */
@Controller
@RequestMapping("/manage/category")
public class CategoryManagerController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;
    @RequestMapping("add_category.do")
    @ResponseBody
    public ServerResponse addCategory(HttpSession session, String categroyName, @RequestParam(value = "parentId",defaultValue = "0") Integer parentId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "需要登录");

        }
        ServerResponse response = iUserService.checkAdminRole(user);
        if (response.isSuccess()) {
            //增加处理分类的逻辑
            return iCategoryService.addCategory(categroyName,parentId);
        }
        return ServerResponse.createByError("无权限操作");
    }
    //更新分类的名称
    @RequestMapping("set_categoryName.do")
    @ResponseBody
    public ServerResponse setCategoryName(HttpSession session, String categroyName, @RequestParam(value = "parentId",defaultValue = "0") Integer parentId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "需要登录");

        }
        ServerResponse response = iUserService.checkAdminRole(user);
        if (response.isSuccess()) {
            //更新名称

        }
        return ServerResponse.createByError("无权限操作");
    }
    //根据id查询分类
    @RequestMapping("get_category.do")
    @ResponseBody
    public ServerResponse<List<Category>> getChildParallerByParentId(HttpSession session, @RequestParam(value = "parentId",defaultValue = "0") Integer parentId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "需要登录");

        }
        ServerResponse response = iUserService.checkAdminRole(user);
        if (response.isSuccess()) {
            //更新名称
            return iCategoryService.getChildParallerByParentId(parentId);
        }
        return ServerResponse.createByError("无权限操作");
    }
    //根据id查询分类,及其子分类
    @RequestMapping("get_deep_category.do")
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpSession session, @RequestParam(value = "parentId",defaultValue = "0") Integer categoryId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "需要登录");

        }
        ServerResponse response = iUserService.checkAdminRole(user);
        if (response.isSuccess()) {
            //查询当前节点的id和递归子节点的id
            //0->1000->10000
            return iCategoryService.selectCategoryAndChildrenByParentId(categoryId);
        }
        return ServerResponse.createByError("无权限操作");
    }
}
