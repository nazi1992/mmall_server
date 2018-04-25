package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2018/3/15 0015.
 */
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService iUserService;
    @RequestMapping(value="/login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession httpSession)
    {
        System.out.print("usernamef dsagdas=g=="+username);
        ServerResponse response =  iUserService.login(username,password);
        if(response.isSuccess())
        {
            httpSession.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }
    @RequestMapping(value="/regist.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user)
    {
        return iUserService.register(user);
    }
    @RequestMapping(value="/checkValid.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str,String type)
    {
        //校验用户名 与邮件是否有效
        return iUserService.checkValid(str,type);
    }
    //获取用户信息
    @RequestMapping(value="/get_user_info.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session)
    {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user!=null)
        {
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createBySuccessMessage("用户未登录，无法获取当前信息");

    }
    //忘记密码
    @RequestMapping(value="/forget_get_question.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username)
    {
        return iUserService.selectQuestion(username);
        //返回用户提示问题，已找回密码
    }
    //检查问题答案是否正确
    @RequestMapping(value="/forget_check_answer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer)
    {
        return iUserService.checkAnswer(username,question,answer);
    }
    //忘记密码,然后重置密码
    @RequestMapping(value="/forget_reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken)
    {
        return iUserService.forgetResetPassword(username,passwordNew,forgetToken);
    }
    @RequestMapping(value="/reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(User user,String passwordOld,String passwordNew)
    {
        return iUserService.resetPassword(user,passwordOld,passwordNew);

    }
    @RequestMapping(value="/update_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateUser(HttpSession session,User user)
    {
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser==null)
        {
            return ServerResponse.createBySuccessMessage("用户未登录，无法获取当前信息");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());//保持id与username不能被改变
        ServerResponse<User> userServerResponse = iUserService.updateInformation(user);
        if(userServerResponse.isSuccess())
        {
            session.setAttribute(Const.CURRENT_USER,userServerResponse.getData());
        }
        return userServerResponse;
    }
    @RequestMapping(value="/get_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getInformation(HttpSession session,User user)
    {
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser==null)
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请强制登录");
        }
        return iUserService.getInformation(currentUser.getId());
    }
}
