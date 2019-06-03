package myutil;

import java.text.SimpleDateFormat;

public class MyDateTime {
    
    private static SimpleDateFormat myDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    
    public static String getDateTime(){
        return myDateFormat.format(System.currentTimeMillis());
    }
    
    public static String getDateTime(SimpleDateFormat df){
        return df.format(System.currentTimeMillis());
    }
    
    public static String getDateTime(Long milliseconds, SimpleDateFormat df){
        return df.format(milliseconds);
    }
}
