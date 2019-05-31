package com.commons.common.utils;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * 日期工具类
 */
public class DateUtil {
    //showType 的取值：例如“yyyy年MM月dd日hh时mm分ss秒”，“yyyy-MM-dd HH:mm:ss” 等
    public static Calendar cal = new GregorianCalendar();
    public static int YEAR = cal.get(Calendar.YEAR);
    public static int MONTH = cal.get(Calendar.MONTH) + 1;
    public static int DAY_OF_MONTH = cal.get(Calendar.DAY_OF_MONTH);
    public static int DAY_OF_WEEK = cal.get(Calendar.DAY_OF_WEEK) - 1;


    //将date按照showType转换成string
    public static String turnDateToString(Date dDate, String showType) {
        DateFormat df = new SimpleDateFormat(showType);
        String dateString = df.format(dDate);
        return dateString;
    }

    //返回将string转换成date类型
    public static Date turnStringToDate(String sDate, String showType) {
        //sDate的格式要和showType相符
        DateFormat df = new SimpleDateFormat(showType);
        Date date = null;
        try {
            date = df.parse(sDate);
        } catch (ParseException e) {
            System.out.println("格式不一致，返回null");
        }
        return date;
    }

    //获得时间差
    public static boolean getTimeDifference(Long timeBegin, Long timeEnd, Long timeStanding) {
        //大于指定时间返回true，否则返回false
        Long difference = timeEnd - timeBegin;
        if (difference >= timeStanding) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 获取一年中的第几周
     * 时间格式'yyyy-MM-dd'
     * cuiqinglong create 2008-3-8
     *
     * @param sDate
     * @return
     */
    public static int getWeek(String sDate) {
        int year = new Integer(sDate.substring(0, 4));
        int month = new Integer(sDate.substring(5, 7)) - 1;
        int day = new Integer(sDate.substring(8, 10));
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        int weekno = cal.get(Calendar.WEEK_OF_YEAR);
        return weekno;
    }

    /**
     * 获取一年中的第几季度
     * 时间格式'yyyy-MM-dd'
     * cuiqinglong create 2008-3-8
     *
     * @param sDate
     * @return
     */
    public static int getQuarter(String sDate) {
        int year = new Integer(sDate.substring(0, 4));
        int month = new Integer(sDate.substring(5, 7));
        switch (month) {
            case 1:
                return 1;
            case 2:
                return 1;
            case 3:
                return 1;
            case 4:
                return 2;
            case 5:
                return 2;
            case 6:
                return 2;
            case 7:
                return 3;
            case 8:
                return 3;
            case 9:
                return 3;
            case 10:
                return 4;
            case 11:
                return 4;
            case 12:
                return 4;
            default:
                return 1;
        }
    }

    /**
     * 获取半年，上半年0，下半年1
     * 时间格式'yyyy-MM-dd'
     * cuiqinglong create 2008-3-8
     *
     * @param sDate
     * @return
     */
    public static int getHYear(String sDate) {
        int month = new Integer(sDate.substring(5, 7));
        if (month >= 1 && month <= 6) {
            return 0;
        } else {
            return 1;
        }
    }

    public static String getYesterday(String sDate) {
        if (sDate.equals("")) {
            sDate = getNowTime("yyyy-MM-dd");
        }
        int year = new Integer(sDate.substring(0, 4));
        int month = new Integer(sDate.substring(5, 7)) - 1;
        int day = new Integer(sDate.substring(8, 10));
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day - 1);
        return DateUtil.turnDateToString(cal.getTime(), "yyyy-MM-dd");
    }

    public static String getTomorrow(String sDate) {
        if (sDate.equals("")) {
            sDate = getNowTime("yyyy-MM-dd");
        }
        int year = new Integer(sDate.substring(0, 4));
        int month = new Integer(sDate.substring(5, 7)) - 1;
        int day = new Integer(sDate.substring(8, 10));
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day + 1);
        return DateUtil.turnDateToString(cal.getTime(), "yyyy-MM-dd");
    }

    public static String getOneWeekAgo(String sDate) {
        if (sDate.equals("")) {
            sDate = getNowTime("yyyy-MM-dd");
        }
        int year = new Integer(sDate.substring(0, 4));
        int month = new Integer(sDate.substring(5, 7)) - 1;
        int day = new Integer(sDate.substring(8, 10));
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day - 7);
        return DateUtil.turnDateToString(cal.getTime(), "yyyy-MM-dd");
    }

    public static String getOneMonthAgo(String sDate) {
        if (sDate.equals("")) {
            sDate = getNowTime("yyyy-MM-dd");
        }
        int year = new Integer(sDate.substring(0, 4));
        int month = new Integer(sDate.substring(5, 7)) - 1;
        int day = new Integer(sDate.substring(8, 10));
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, day);
        return DateUtil.turnDateToString(cal.getTime(), "yyyy-MM-dd");
    }

    public static String getOneMonthAfter(String sDate) {
        if (sDate.equals("")) {
            sDate = getNowTime("yyyy-MM-dd");
        }
        int year = new Integer(sDate.substring(0, 4));
        int month = new Integer(sDate.substring(5, 7)) - 1;
        int day = new Integer(sDate.substring(8, 10));
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month + 1);
        cal.set(Calendar.DAY_OF_MONTH, day);
        Date monthLastDateTime = cal.getTime();

        Calendar c = Calendar.getInstance();
        c.setTime(DateUtil.turnStringToDate(sDate, "yyyy-MM-dd"));
        c.add(Calendar.MONTH, 1);
        Date endDate = c.getTime();

        //返回月份不能跨两个月,跨两个月就取下个月的最后一天
        if (monthLastDateTime.after(DateUtil.getMonthLastDateTime(endDate))) {
            monthLastDateTime = DateUtil.getMonthLastDateTime(endDate);
        }
        return DateUtil.turnDateToString(monthLastDateTime, "yyyy-MM-dd");
    }

    public static String getSomeDaysAgo(String sDate, int days) {
        if (sDate.equals("")) {
            sDate = getNowTime("yyyy-MM-dd");
        }
        int year = new Integer(sDate.substring(0, 4));
        int month = new Integer(sDate.substring(5, 7)) - 1;
        int day = new Integer(sDate.substring(8, 10));
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day - days);
        return DateUtil.turnDateToString(cal.getTime(), "yyyy-MM-dd");
    }

    public static String getSomeSecondsAgo(String sDate, int seconds) {
        if (sDate.equals("")) {
            sDate = getNowTime("yyyy-MM-dd HH:mm:ss");
        }
        int year = new Integer(sDate.substring(0, 4));
        int month = new Integer(sDate.substring(5, 7)) - 1;
        int day = new Integer(sDate.substring(8, 10));
        int hour = new Integer(sDate.substring(11, 13));
        int minute = new Integer(sDate.substring(14, 16));
        int second = new Integer(sDate.substring(17, 19));
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second - seconds);
        return DateUtil.turnDateToString(cal.getTime(), "yyyy-MM-dd HH:mm:ss");
    }

    public static String getSomeSecondsAfter(String sDate, int seconds) {
        if (sDate.equals("")) {
            sDate = getNowTime("yyyy-MM-dd HH:mm:ss");
        }
        int year = new Integer(sDate.substring(0, 4));
        int month = new Integer(sDate.substring(5, 7)) - 1;
        int day = new Integer(sDate.substring(8, 10));
        int hour = new Integer(sDate.substring(11, 13));
        int minute = new Integer(sDate.substring(14, 16));
        int second = new Integer(sDate.substring(17, 19));
        //logger.debug("year:"+year+" month:"+month+" day:"+day+" hour:"+hour+" minute:"+minute+" second:"+second);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second + seconds);
        return DateUtil.turnDateToString(cal.getTime(), "yyyy-MM-dd HH:mm:ss");
    }

    public static Date getAfterDate(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + days);
        return calendar.getTime();
    }

    public static Date getBeforeDate(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - days);
        return calendar.getTime();
    }

    /**
     * 把日期和时间组合成一个日期完整形式
     *
     * @param date yyyy-MM-dd
     * @param time HH:mm:ss
     * @return 拼接好的日期形式
     */
    public static Date addDateAndTime(Date date, Date time) {
        SimpleDateFormat sdfD = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfT = new SimpleDateFormat("HH:mm:ss");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formatDate = sdfD.format(date);
        String formatTime = sdfT.format(time);
        try {
            Date parse = df.parse(formatDate + " " + formatTime);
            return parse;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    //以showType的格式，返回String类型的当前时间
    public static String getNowTime(String showType) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(showType);
        return sdf.format(date);
    }

    /**
     * 当前日期
     *
     * @return
     */
    public static Date getNowTime() {
        return new Date();
    }

    /**
     * 当前日期 不含时分秒
     *
     * @return
     */
    public static Date getNowDay() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public static Date getNowDayLastDateTime() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 获取本周 星期几
     *
     * @return
     * @link #DAY_OF_WEEK
     */
    public static Date getWeek(int day) {
        Calendar c = Calendar.getInstance();
        c.setTime(getNowDay());
        c.add(Calendar.DAY_OF_MONTH, -1);//china week
        c.set(Calendar.DAY_OF_WEEK, day);
        return c.getTime();
    }

    public static Date getMonth() {
        Calendar c = Calendar.getInstance();
        c.setTime(getNowDay());
        c.set(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }

    /**
     * 获取本周一
     *
     * @return
     */
    public static Date getWeekMonday() {
        return getWeek(Calendar.MONDAY);
    }

    /**
     * 获取本周一 但不跨越， 若跨越则按本月第一天
     */
    public static Date getWeekMondayInMonth() {
        Date wm = getWeekMonday();
        Date month = getMonth();
        if (wm.getTime() < month.getTime()) {
            return month;
        }
        return wm;
    }

    /**
     * 当月第一天0秒时刻
     */
    public static Date getMonthFirstDateTime() {
        Calendar c = Calendar.getInstance();
        return getMonthFirstDateTime(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1);
    }

    public static Date getMonthFirstDateTime(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return getMonthFirstDateTime(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1);
    }

    public static Date getMonthFirstDateTime(Integer year, Integer month) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.DAY_OF_MONTH, c.getMinimum(Calendar.DAY_OF_MONTH));
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 当月最后一秒时刻
     */
    public static Date getMonthLastDateTime() {
        Calendar c = Calendar.getInstance();
        return getMonthLastDateTime(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1);
    }

    public static Date getMonthLastDateTime(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return getMonthLastDateTime(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1);
    }

    public static Date getMonthLastDateTime(Integer year, Integer month) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, 0);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 两个时间之间相差距离多少天
     * 不分顺序
     *
     * @return 相差天数
     */
    public static Long getBetweenDays(String one, String two) {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            return getBetweenDays(df.parse(one), df.parse(two));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static long getBetweenDays(Date one, Date two) {
        long days = 0;
        long time1 = one.getTime();
        long time2 = two.getTime();
        long diff;
        if (time1 < time2) {
            diff = time2 - time1;
        } else {
            diff = time1 - time2;
        }
        days = diff / (1000 * 60 * 60 * 24);
        return days;
    }

    /**
     * 获取传入月份的 获取当前月份的第几周的周几的 日期
     *
     * @param inDate      日期
     * @param weekOfMonth 月份的第几周
     * @param week        星期几
     * @return
     */
    public static Date getDayByMonthWeek(String inDate, int weekOfMonth, int week) {
        Date date = DateUtil.turnStringToDate(inDate, "yyyy-MM-dd");
        int c = weekOfMonth - getWeekOfMonth(date);//周间隔
        int d = week - getWeek(date);//日间隔
        int days = c * 7 + d;
        Date weekDateEnd = DateUtil.getAfterDate(date, days);

        //不跨月处理
        Date monthFirstDateTime = DateUtil.getMonthFirstDateTime(date);
        Date monthLastDateTime = DateUtil.getMonthLastDateTime(date);
        //跨月就取当月第一天或当月最后一天
        if (weekDateEnd.before(monthFirstDateTime)) {
            weekDateEnd = monthFirstDateTime;
        }
        if (weekDateEnd.after(monthLastDateTime)) {
            weekDateEnd = monthLastDateTime;
        }
        return weekDateEnd;
    }

    /**
     * 获取当前日期是当月的第几周
     */
    public static int getWeekOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
        return weekOfMonth;
    }

    /**
     * 获取当前日期是周几
     * 2星期一，3星期二，4星期三，5星期四，6星期五， 7星期六，1星期日
     */
    public static int getWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int weekOfDay = calendar.get(Calendar.DAY_OF_WEEK);
        return weekOfDay;
    }

}

