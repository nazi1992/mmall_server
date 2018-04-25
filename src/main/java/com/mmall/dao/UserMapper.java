package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUserName(String username);
    User checkLogin(@Param("username")String username,@Param("password") String password); //@Param 重新定义注入时该填的值
    int checkEmail(String email);

    //通过用户名查询提示问题
    String selectQuestionByUsername(String username);
    int checkAnwser(String username,String question,String answer);
    int updatePasswordByUsername(@Param("username")String username,@Param("password") String passwordNew);

    int checkPassword(String password,Integer userId);

    int checkEmailByUserId(String email,Integer userId);
}