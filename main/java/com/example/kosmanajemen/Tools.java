package com.example.kosmanajemen;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Tools {

    public static String getCurrentDateIndonesia(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"));
        Date date = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date);
    }
    public static String getYesterdayDateIndonesia(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"));
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterday  = calendar.getTime();
        // 0 equal, 1 greater, -1 less
        SimpleDateFormat simpleDateFormat  = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(yesterday);
    }
    public static String getTomorrowDateIndonesia(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow  = calendar.getTime();
        // 0 equal, 1 greater, -1 less
        SimpleDateFormat simpleDateFormat  = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(tomorrow);
    }
    public static String getCurrentTimeIndonesia(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"));
        Date date = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        return simpleDateFormat.format(date);
    }public static String getCurrentTimeStampIndonesia(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"));
        Date date = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }public static int compareDateWithCurrentDate(Date date){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"));
        Date currentDate = calendar.getTime();
        // 0 equal, 1 greater, -1 less
        SimpleDateFormat simpleDateFormat  = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(currentDate).compareTo(simpleDateFormat.format(date));
    }
    public static String convertToDate(int year, int month, int day){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"));
        calendar.set(year, month, day);
        Date date = calendar.getTime();
        // 0 equal, 1 greater, -1 less
        SimpleDateFormat simpleDateFormat  = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date);
    }
}
