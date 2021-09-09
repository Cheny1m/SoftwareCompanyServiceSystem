package cn.edu.sicnu.cs.utils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 时间工具类,判断是不是今天的时间,昨天的时间,上一周的时间,上一个月的时间
 * @Classname TimeUtils
 * @Description TODO
 * @Date 2020/12/14 22:11
 * @Created by Huan
 */
public class TimeUtils {
    /**
     * 得到今天的起始时间戳
     * @return
     */
    public static List<Timestamp> today(){
        Calendar calendar = Calendar.getInstance();
        clearCalendar(calendar,Calendar.HOUR_OF_DAY,Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND);
        Timestamp beginTimestamp = new Timestamp(calendar.getTimeInMillis());
        calendar.add(Calendar.DAY_OF_MONTH, +1);
        Timestamp endTimestamp = new Timestamp(calendar.getTimeInMillis());
        List<Timestamp> today = new ArrayList<Timestamp>();
        today.add(beginTimestamp);
        today.add(endTimestamp);
        return today;
    }

    /**
     * 得到昨天的起始时间戳
     * @return
     */
    public static List<Timestamp> yesterday(){
        Calendar calendar = Calendar.getInstance();
        clearCalendar(calendar,Calendar.HOUR_OF_DAY,Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND);
        Timestamp endTimestamp = new Timestamp(calendar.getTimeInMillis());
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Timestamp beginTimestamp = new Timestamp(calendar.getTimeInMillis());
        List<Timestamp> today = new ArrayList<Timestamp>();
        today.add(beginTimestamp);
        today.add(endTimestamp);
        return today;
    }

    /**
     * 得到上一周的起始时间戳
     * @return
     */
    public static List<Timestamp> lastWeek(){
        Calendar calendar = Calendar.getInstance();
        clearCalendar(calendar,Calendar.HOUR_OF_DAY,Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND);
//        calendar.add(Calendar.DAY_OF_MONTH, );
        Timestamp endTimestamp = new Timestamp(calendar.getTimeInMillis());
        // 这里可能有问题
        calendar.add(Calendar.WEEK_OF_MONTH, -1);
        Timestamp beginTimestamp = new Timestamp(calendar.getTimeInMillis());
        List<Timestamp> today = new ArrayList<Timestamp>();
        today.add(beginTimestamp);
        today.add(endTimestamp);
        return today;
    }

    /**
     * 得到前一个月的起始时间戳
     * @return
     */
    public static List<Timestamp> lastMouth(){
        Calendar calendar = Calendar.getInstance();
        clearCalendar(calendar,Calendar.HOUR_OF_DAY,Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND);
//        calendar.add(Calendar.DAY_OF_MONTH, +1);
        Timestamp endTimestamp = new Timestamp(calendar.getTimeInMillis());
        // 这里可能有问题
        calendar.add(Calendar.MONTH, -1);
        Timestamp beginTimestamp = new Timestamp(calendar.getTimeInMillis());
        List<Timestamp> today = new ArrayList<Timestamp>();
        today.add(beginTimestamp);
        today.add(endTimestamp);
        return today;
    }

    public static List<Timestamp> thisWeek() {
        Calendar calendar = Calendar.getInstance();

        clearCalendar(calendar,Calendar.HOUR_OF_DAY,Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND);
        // 这里可能有问题
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MILLISECOND, -1);
        Timestamp endTimestamp = new Timestamp(calendar.getTimeInMillis());
        calendar.add(Calendar.WEEK_OF_MONTH, -1);
        calendar.add(Calendar.MILLISECOND, +1);
        Timestamp beginTimestamp = new Timestamp(calendar.getTimeInMillis());
        List<Timestamp> today = new ArrayList<Timestamp>();
        today.add(beginTimestamp);
        today.add(endTimestamp);
        return today;
    }

    /**
     * 判断时间是不是今天的时间
     * @param nowTimestamp
     * @return
     */
    public static boolean isToday(long nowTimestamp){
        List<Timestamp> today = today();
        return today.get(0).getTime()<=nowTimestamp&&nowTimestamp<=today.get(1).getTime();
    }

    /**
     * 判断时间戳是不是昨天的时间
     * @param nowTimestamp
     * @return
     */
    public static boolean isYesterDay(long nowTimestamp){
        List<Timestamp> today = yesterday();
        return today.get(0).getTime()<=nowTimestamp&&nowTimestamp<=today.get(1).getTime();
    }

    /**
     * 判断时间戳是不是上一周的时间
     * @param nowTimestamp
     * @return
     */
    public static boolean isLastWeek(long nowTimestamp){
        List<Timestamp> today = lastWeek();
        return today.get(0).getTime()<=nowTimestamp&&nowTimestamp<=today.get(1).getTime();
    }

    /**
     * 判断当前时间是不是上一个月的时间
     * @param nowTimestamp
     * @return
     */
    public static boolean islastMouth(long nowTimestamp){
        List<Timestamp> today = lastMouth();
        return today.get(0).getTime()<=nowTimestamp&&nowTimestamp<=today.get(1).getTime();
    }



    public static void main(String[] args) {
        List<Timestamp> today = thisWeek();
        Calendar calendar = Calendar.getInstance();
        for (Timestamp timestamp : today) {
            calendar.setTime(timestamp);
            System.out.println(timestamp.getTime());
            System.out.println(calendar.getTime());
        }
    }
    private static void clearCalendar(Calendar c, int ... fields) {
        for (int field : fields) {
            c.set(field, 0);
        }
    }


}
