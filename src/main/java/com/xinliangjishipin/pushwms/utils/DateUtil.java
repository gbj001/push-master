package com.xinliangjishipin.pushwms.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static Date strToDate(String strDate, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        //必须捕获异常
        try {
            Date date = simpleDateFormat.parse(strDate);
            return date;
        } catch (ParseException px) {
            px.printStackTrace();
        }
        return null;
    }

    public static int compare_date(String date1, String date2, String format) {
        DateFormat df = new SimpleDateFormat(format);
        try {
            Date dt1 = df.parse(date1);
            Date dt2 = df.parse(date2);
            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    public static boolean dateInStartTimeAndEndTime(String date1, String startTime, String endTime, String format){
        return compare_date(date1, startTime,format) >=0 && compare_date(date1, endTime, format) <=0;
    }
}
