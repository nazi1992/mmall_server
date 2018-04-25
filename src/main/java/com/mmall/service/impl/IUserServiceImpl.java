package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import com.sun.corba.se.spi.activation.Server;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * Created by Administrator on 2018/3/15 0015.
 */
@Service("iUserService")

public class IUserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public ServerResponse<User> login(String username, String password) {
        try
        {
            int resultCount = userMapper.checkUserName(username);
            System.out.print("resultCount=="+resultCount);
            if(resultCount == 0)
            {
                return ServerResponse.createByError("用户名不存在");
            }
            String md5Password = MD5Util.MD5EncodeUtf8(password);//将传入的密码进行md5加密
            User user = userMapper.checkLogin(username,md5Password);
            if(user == null)
            {
                return ServerResponse.createByError("密码错误");
            }
            user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
            return ServerResponse.createBySuccess("登录成功",user);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    public ServerResponse<String> register(User user)
    {
        ServerResponse<String>  validResponse = this.checkValid(user.getUsername(),Const.USERNAME);
        if(!validResponse.isSuccess())
        {
            return validResponse;
        }
         validResponse = this.checkValid(user.getUsername(),Const.EMAIL);
        if(!validResponse.isSuccess())
        {
            return validResponse;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int   resultCount = userMapper.insert(user);
        if(resultCount==0)//插入成功的行数
        {
            return ServerResponse.createByError("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }
    public ServerResponse<String> checkValid(String str,String type)
    {
        if(StringUtils.isNotBlank(type))
        {
            if(Const.USERNAME.equals(type))
            {
                int resultCount= userMapper.checkUserName(str);
                if(resultCount>0)
                {
                    return ServerResponse.createByError("用户名已存在");
                }

            }
            if(Const.EMAIL.equals(type))
            {
               int resultCount =  userMapper.checkEmail(str);
                if(resultCount>0)
                {
                    return ServerResponse.createByError("email已存在");
                }
            }
            //
        }
        else
        {
            return ServerResponse.createByError("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }
    public ServerResponse<String> selectQuestion(String username)
    {
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess())
        {
            //校验，用户名不存在，则无法选择提示问题
            return ServerResponse.createByError("用户不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if(StringUtils.isNotBlank(question))
        {
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByError("找回密码的问题是空的");
    }
    public ServerResponse<String> checkAnswer(String username,String question,String answer)
    {
        int result = userMapper.checkAnwser(username,question,answer);
        if(result>0)
        {
            //说明问题及问题答案是和这个用户的，并且是正确的
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.serKey("token_"+username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByError("问题的答案错误");
    }
    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken)
    {
        if(StringUtils.isBlank(forgetToken))
        {
            return ServerResponse.createByError("参数错误，token需要传递");
        }
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess())
        {
            //用户不存在
            return ServerResponse.createByError("用户不存在");
        }
        java.lang.String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if(StringUtils.isBlank(token))
        {
            return ServerResponse.createByError("token 无效");
        }
        if(StringUtils.equals(forgetToken,token))
        {
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username,md5Password);
            if(rowCount>0)
            {
                return ServerResponse.createBySuccess("修改密码成功");
            }

        }
        else
        {
            return ServerResponse.createByError("token错误，请重新获取重置密码的token");
        }
        return ServerResponse.createByError("修改密码失败");

    }
    public ServerResponse<String> resetPassword(User user,String passwordOld,String passwordNew)
    {
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        //防止横向越权，要检验一下这个密码的旧用户，
        if(resultCount==0)
        {
            return ServerResponse.createByError("旧密码错误");
        }
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount>0)
        {
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createBySuccessMessage("密码更新失败");

    }
    //更新用户信息
    public ServerResponse<User> updateInformation(User user)
    {
        //username不能被更新
        //而且需要判断email是否存在，如果存在不能是当前用户的email
        int result = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(result>0)
        {
            return ServerResponse.createByError("邮箱已被其他人使用");
        }
        User updateUser = new User();
        updateUser.setAnswer(user.getAnswer());
        updateUser.setPhone(user.getPhone());
        updateUser.setEmail(user.getEmail());
        updateUser.setQuestion(user.getQuestion());
        int i = userMapper.updateByPrimaryKeySelective(updateUser);
        if(i>0)
        {
            return ServerResponse.createBySuccess("更新成功",updateUser);
        }
        return ServerResponse.createByError("更新失败");

    }

    public ServerResponse<User> getInformation(Integer userId)
    {
        User user = userMapper.selectByPrimaryKey(userId);
        if(user==null)
        {
            return ServerResponse.createByError("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);//将返回的用户信息中的密码置为空
        return ServerResponse.createBySuccess(user);
    }

    public ServerResponse checkAdminRole(User user)
    {
        if(user!=null&&user.getRole().intValue()==Const.Role.ROLE_ADMIN)
        {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError("没有权限");
    }
}
