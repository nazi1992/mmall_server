package com.mmall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/3/15 0015.
 */
//
 @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
 //改注解的意思是，当某一字段为null，不输出到前段页面
public class ServerResponse<T> implements Serializable {
    private int status;
    private String msg;
    private T data;

    private ServerResponse( int status) {
        this.status = status;

    }
    private ServerResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }
    private ServerResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }
    private ServerResponse( int status ,String msg, T data) {
        this.msg = msg;
        this.data = data;
        this.status = status;

    }
    //输出到页面后不带此字段,使之不在json序列化结果中
    @JsonIgnore
    public boolean isSuccess()
    {
        return this.status==ResponseCode.SUCCESS.getCode();
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    /**
     * 返回成功对象
     * @param <T>
     * @return
     */
    public static <T> ServerResponse<T> createBySuccess(){
            return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }
    public static <T> ServerResponse<T> createBySuccessMessage(String msg){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
    }
    public static <T> ServerResponse<T> createBySuccess(T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }
    public static <T> ServerResponse<T> createBySuccess(String msg,T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
    }
    /**
     * 返回失败对象
     */
    public static <T> ServerResponse<T> createByError()
    {
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
    }

    public static <T> ServerResponse<T> createByError(String msg)
    {
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),msg);
    }
    public static <T> ServerResponse<T> createByErrorCodeMessage(int errorCode,String errorMsg)
    {
        return new ServerResponse<T>(errorCode,errorMsg);
    }
}
