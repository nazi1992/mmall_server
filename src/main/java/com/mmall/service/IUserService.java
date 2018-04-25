package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

/**
 * Created by Administrator on 2018/3/15 0015.
 */
public interface IUserService {
    ServerResponse<User> login(String username, String password);
    ServerResponse<String> register(User user);
    ServerResponse<String> checkValid(String str,String type);
    ServerResponse<String> selectQuestion(String username);
    ServerResponse<String> checkAnswer(String username,String question,String answer);
    ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken);
    ServerResponse<String> resetPassword(User user,String passwordOld,String passwordNew);

    //更新用户
    ServerResponse<User> updateInformation(User user);
    //获取用户信息
    ServerResponse<User> getInformation(Integer userId);
    //判断是否管理员
    ServerResponse checkAdminRole(User user);
}
