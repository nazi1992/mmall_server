package com.mmall.util;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2018/4/27 0027.
 */
public class BigdelcimalUtil {
    private BigdelcimalUtil(){}// 防止外部实例化

    //在商业运算中采取bigdecimal
    public static BigDecimal add(double a,double b)
    {
        BigDecimal bigDecimala = new BigDecimal(Double.toString(a));
        BigDecimal bigDecimalb = new BigDecimal(Double.toString(b));
        return bigDecimala.add(bigDecimalb);
    }//加
    public static BigDecimal sub(double a,double b)
    {
        BigDecimal bigDecimala = new BigDecimal(Double.toString(a));
        BigDecimal bigDecimalb = new BigDecimal(Double.toString(b));
        return bigDecimala.subtract(bigDecimalb);
    }//减
    public static BigDecimal mul(double a,double b)
    {
        BigDecimal bigDecimala = new BigDecimal(Double.toString(a));
        BigDecimal bigDecimalb = new BigDecimal(Double.toString(b));
        return bigDecimala.multiply(bigDecimalb);
    }//乘
    public static BigDecimal div(double a,double b)
    {
        BigDecimal bigDecimala = new BigDecimal(Double.toString(a));
        BigDecimal bigDecimalb = new BigDecimal(Double.toString(b));
        return bigDecimala.divide(bigDecimalb,2,BigDecimal.ROUND_HALF_UP);
        //在除法中的结果中四舍五入并保留两位小数
    }//除

}
