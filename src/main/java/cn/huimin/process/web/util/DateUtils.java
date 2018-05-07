package cn.huimin.process.web.util;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/11/30.
 */
public class DateUtils {
    private static final String model = "yyyy-MM-dd HH:mm:ss";

    //日期转化转为字符串
    public static String dateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(model);
        return sdf.format(date);
    }


   public static Date TimeToDate(Long time){
       return new Date(time);
   }

    /**
     * 根据小时创建对应的dateTime时间
     *
     * @param hour
     * @return
     */
    public static DateTime createDateTime(String hour) {
        return new DateTime(DateUtils.calHour(hour)+nowTime());
    }

    /**
     * 获取当前时间Date格式
     */
    public static Date now() {
        return new Date(System.currentTimeMillis());
    }

    /**
     * 获取时间毫秒值
     *
     * @return
     */
    public static Long nowTime() {
        return System.currentTimeMillis();
    }

    /**
     * 毫秒转换为日期
     *
     * @param timeSec
     * @return
     */
    public static Date timeToDate(Long timeSec) {
        return new Date(timeSec);
    }

    /**
     * 日期转化为date
     *
     * @param date
     * @return
     */
    public static Long dateToTime(Date date) {
        return date.getTime();
    }


    /**
     * 计算小时的毫秒值
     * @param hour
     * @return
     */
    public static Long calHour(String hour){
        return  Long.valueOf(hour) * 60 * 60 * 1000;
    }

    /**
     * 给定date日期和小时计算对应的日期
     *
     * @param date 时间日期
     * @param hour 小时
     * @return
     */
    public static Date calTime(Date date, String hour) {
        return timeToDate(dateToTime(date)+calHour(hour));
    }


}
