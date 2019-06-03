package myutil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;
import org.apache.log4j.*;

public final class MyLogger {

    private static String errorsLoggerName;
    private static String errorsLoggerFile;
    private static String errorsLoggerLayoutPat;
    private static String errorsLoggerLevel;    
    
    private static String statusLoggerName;
    private static String statusLoggerFile;
    private static String statusLoggerLayoutPat;
    private static String statusLoggerLevel;
    
    private static String eventsLoggerName;
    private static String eventsLoggerFile;
    private static String eventsLoggerLayoutPat;
    private static String eventsLoggerLevel;
    
    private static String resultsLoggerName;
    private static String resultsLoggerFile;
    private static String resultsLoggerLayoutPat;
    private static String resultsLoggerLevel;
    private static Logger logger;
    
    public static Logger errorsLogger;
    public static Logger statusLogger;
    public static Logger eventsLogger;
    public static Logger resultsLogger;
    
    private static final MyLogger MY_LOGGER = new MyLogger();
    
    public static MyLogger getInstance(){
        return MY_LOGGER;
    }
    
    private MyLogger(){
        readProperties();
    }
    
    public static Logger getErrorsLogger(){
        if(errorsLogger instanceof Logger){
            return errorsLogger;
        } else {
            errorsLogger = getMyLogger(errorsLoggerName, errorsLoggerFile, errorsLoggerLayoutPat, errorsLoggerLevel);
            return errorsLogger;
        }
    }
    
    public static Logger getStatusLogger(){
        if(statusLogger instanceof Logger){
            return statusLogger;
        } else {
            statusLogger = getMyLogger(statusLoggerName, statusLoggerFile, statusLoggerLayoutPat, statusLoggerLevel);
            return statusLogger;
        }
    }
    
    public static Logger getEventsLogger(){
        if(eventsLogger instanceof Logger){
            return eventsLogger;
        } else {
            eventsLogger = getMyLogger(eventsLoggerName, eventsLoggerFile, eventsLoggerLayoutPat, eventsLoggerLevel);
            return eventsLogger;
        }
    }
    
    public static Logger getResultsLogger(){
        if(resultsLogger instanceof Logger){
            return resultsLogger;
        } else {
            resultsLogger = getMyLogger(resultsLoggerName, resultsLoggerFile, resultsLoggerLayoutPat, resultsLoggerLevel);
            return resultsLogger;
        }
    }    

    private static Logger getMyLogger(String loggerName, String logFile, String layoutPat, String logLevel){
        File file = new File(logFile);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        PatternLayout layout = new PatternLayout(layoutPat);
        logger = Logger.getLogger(loggerName);
        try {
            RollingFileAppender appender = new RollingFileAppender(layout, logFile, true);
            appender.setMaxFileSize("1MB");
            appender.setMaxBackupIndex(10);
            logger.addAppender(appender);
            switch(logLevel.toUpperCase()){
                case "ALL"   : {logger.setLevel(Level.ALL);    break;}
                case "DEBUG" : {logger.setLevel(Level.DEBUG);  break;}
                case "INFO"  : {logger.setLevel(Level.INFO);   break;}
                case "WARN"  : {logger.setLevel(Level.WARN);   break;}
                case "ERROR" : {logger.setLevel(Level.ERROR);  break;}
                case "FATAL" : {logger.setLevel(Level.FATAL);  break;}
                case "OFF"   : {logger.setLevel(Level.OFF);    break;}
                default      : {logger.setLevel(Level.DEBUG);  break;}
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return logger;
    }
    
    
    public void readProperties() {
        
        Properties prop = new Properties();     

        try {
            InputStream in = new BufferedInputStream (new FileInputStream("logger.properties"));
            prop.load(in);
            Iterator<String> it=prop.stringPropertyNames().iterator();
            while(it.hasNext()){
                String key=it.next();
                System.out.println(key + " : " + prop.getProperty(key));
                switch(key){
                    case "errorsLoggerName"     : { errorsLoggerName       = prop.getProperty(key);  break;}
                    case "errorsLoggerFile"     : { errorsLoggerFile       = prop.getProperty(key);  break;}
                    case "errorsLoggerPattern"  : { errorsLoggerLayoutPat  = prop.getProperty(key);  break;}
                    case "errorsLoggerLevel"    : { errorsLoggerLevel      = prop.getProperty(key);  break;}
                    case "statusLoggerName"     : { statusLoggerName       = prop.getProperty(key);  break;}
                    case "statusLoggerFile"     : { statusLoggerFile       = prop.getProperty(key);  break;}
                    case "statusLoggerPattern"  : { statusLoggerLayoutPat  = prop.getProperty(key);  break;}
                    case "statusLoggerLevel"    : { statusLoggerLevel      = prop.getProperty(key);  break;}
                    case "eventsLoggerName"     : { eventsLoggerName       = prop.getProperty(key);  break;}
                    case "eventsLoggerFile"     : { eventsLoggerFile       = prop.getProperty(key);  break;}
                    case "eventsLoggerPattern"  : { eventsLoggerLayoutPat  = prop.getProperty(key);  break;}
                    case "eventsLoggerLevel"    : { eventsLoggerLevel      = prop.getProperty(key);  break;}
                    case "resultsLoggerName"    : { resultsLoggerName      = prop.getProperty(key);  break;}
                    case "resultsLoggerFile"    : { resultsLoggerFile      = prop.getProperty(key);  break;}
                    case "resultsLoggerPattern" : { resultsLoggerLayoutPat = prop.getProperty(key);  break;}
                    case "resultsLoggerLevel"   : { resultsLoggerLevel     = prop.getProperty(key);  break;}
                    default                     : { break;}
                }
            }
            in.close();
            
            String timestamp = MyDateTime.getDateTime();
            statusLoggerFile = statusLoggerFile.replace("timestamp", timestamp);
            errorsLoggerFile = errorsLoggerFile.replace("timestamp", timestamp);
            statusLogger = getStatusLogger();
            errorsLogger = getErrorsLogger();    
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
