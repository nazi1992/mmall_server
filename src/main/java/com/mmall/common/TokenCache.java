package com.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2018/4/2 0002.
 */
public class TokenCache {
    public static final String TOKEN_PREFIX = "token_";
    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);
    //构建本地Cache,设置缓存的初始化容量  最大容量，当超过10000时，使用LRU算法
    private static LoadingCache<String,String> localCache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS).build(new CacheLoader<String, String>() {
        @Override
        public String load(String s) throws Exception {
            //默认的数据加载实现，当调用get取值的 时候，如果key没有对应的值，就调用这个方法进行加载
            return "null";
        }
    });
    public static  void serKey(String key,String value)
    {
        localCache.put(key
        ,value);
    }
    public static String getKey(String key)
    {
        String value = null;
        try{
            value = localCache.get(key);
            if("null".equals(value))
            {
                return null;
            }
            return value;
        }catch (Exception e)
        {
            logger.error("localCache get error",e);
        }
        return null;
    }
}
