package com.mmall.util;

import org.apache.commons.codec.binary.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * Created by Administrator on 2018/4/9 0009.
 */
public class DateTimeUtil {

    public  static    String  DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static Date strToTime(String dataTimeStr, String formatStr)
    {
        DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern(formatStr);
        DateTime dateTime= dateTimeFormat.parseDateTime(dataTimeStr);
        return  dateTime.toDate();

    }
    public static String timeToStr(Date date,String formatStr)
    {
        if(date==null)
        {
            return org.apache.commons.lang3.StringUtils.EMPTY;
        }
        DateTime dateTime =new  DateTime(date);
        return dateTime.toString(formatStr);
    }

    public static Date strToTime(String dataTimeStr)
    {
        DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern(DATE_FORMAT);
        DateTime dateTime= dateTimeFormat.parseDateTime(dataTimeStr);
        return  dateTime.toDate();

    }
    public static String timeToStr(Date date)
    {
        if(date==null)
        {
            return org.apache.commons.lang3.StringUtils.EMPTY;
        }
        DateTime dateTime =new  DateTime(date);
        return dateTime.toString(DATE_FORMAT);
    }
}
