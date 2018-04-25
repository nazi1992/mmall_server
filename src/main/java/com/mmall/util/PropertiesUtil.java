package com.mmall.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by geely
 */
public class PropertiesUtil {

   private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);
    private static Properties props;

    static
    {
        //静态块，当该类被加载到java虚拟机时，被调用，只执行一次,执行顺序优先于普通代码块{}，优先于构造函数
        String fileName = "mmall,properties";
        props = new Properties();
        try {
            props.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName),"UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getPropertie(String key)
    {
        String value = props.getProperty(key.trim());
        if(value!=null)
        {
            return value.trim();
        }
        return null;

    }

    public static String getPropertie(String key,String defaultValue)
    {
        String value = props.getProperty(key.trim());
        if(value!=null)
        {
            return value.trim();
        }
        return defaultValue;

    }

}
