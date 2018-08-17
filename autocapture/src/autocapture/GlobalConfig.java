/**
 *
 * handsome boy
 */

package autocapture;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author haind25 <Apllication Development Department - Viettel Global>
 * @since Jun 11, 2015
 * @mail haind25@viettel.com.vn
 */
public class GlobalConfig {
    public final static String DEFAULT_CONFIG_APP = "etc/config.properties";
    
    
    private static final Object obj = new Object();
    
    public static String configFilePath;
    
    public static ResourceBundle resourceBundle;
    public static String queryPath;
    public static String databasePath;
    public static String schedulerPath;
    public static String makeFilePath;
    public static String log4jPath;
    public static String tempPath;
    
    public static int excutorLength;
    public static int cdrWriterThread;
    public static Boolean remakefileOnError = null;
    public static int fetchSize = -1;
    
    private static Boolean push2Hdfs = null;
    private static Boolean delSrcAfter2Hdfs = null;
    private static String hdfsPushUrl = null;
    
    private static String tempFolder = null;
    
    
    
    private static void config(InputStream inputStream) {
        try {
            resourceBundle = new PropertyResourceBundle(inputStream);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GlobalConfig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GlobalConfig.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(GlobalConfig.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void config(String configFile) {
        configFilePath = configFile;
        FileInputStream fis;
        try {
            fis = new FileInputStream(configFile);
            config(fis);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GlobalConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public static String get(String key) {
        synchronized(obj) {
            try{
                return resourceBundle.getString(key);
            } catch(MissingResourceException ex){
            }
            return null;
        }
    }
    
    public static String getQueryPath() {
        if (queryPath == null){
            queryPath = get("path_query");
        }
        return queryPath;
    }

    public static String getDatabasePath() {
        if (databasePath == null){
            databasePath = get("path_database");
        }
        return databasePath;
    }
    
    public static String getSchedulerPath() {
        if (schedulerPath == null){
            schedulerPath = get("path_scheduler");
        }
        return schedulerPath;
    }
    
    public static String getMakeFilePath() {
        if (makeFilePath == null){
            makeFilePath = get("path_make_file");
        }
        return makeFilePath;
    }

    public static int getExcutorLength() {
        if (excutorLength == 0){
            excutorLength = 3;
            String tm = get("thread_size_query_database");
            if (tm != null) {
                excutorLength = Integer.parseInt(tm);
            }
        }
        return excutorLength;
    }
    
    public static int getInt(String key, int defaultValue) {
        int value = defaultValue;
        String tm = get(key);
        if (tm != null) {
            value = Integer.parseInt(tm);
        }
        
        return value;
    }

    static String getLog4jPath() {
        if (log4jPath == null){
            log4jPath = get("path_log4j");
        }
        return log4jPath;
    }

    public static int getCdrWriterThread() {
        if (cdrWriterThread == 0){
            cdrWriterThread = CommonUtil.getNumCoreCPU();
            String tm = get("thread_size_cdr_writer");
            if (tm != null) {
                cdrWriterThread = Integer.parseInt(tm);
            }
        }
        return cdrWriterThread;
    }
    
    public static boolean remakeFileOnError() {
        if (remakefileOnError == null){
            int tm = getInt("remakefile_on_error", 1 );
            remakefileOnError = tm > 0;
        }
        return remakefileOnError;
    }

    public static int getFetchSize() {
         if (fetchSize < 0 ){
            fetchSize = getInt("fetch_size", 1000 );
        }
        return fetchSize;
    }
    
   public static boolean push2Hdfs() {
        if (push2Hdfs == null){
            int tm = getInt("hdfs_push", 0);
            push2Hdfs = tm > 0;
        }
        return push2Hdfs;
    }
   
    public static boolean delSrcAfter2Hdfs() {
        if (delSrcAfter2Hdfs == null){
            int tm = getInt("hdfs_del_after_push", 0);
            delSrcAfter2Hdfs = tm > 0;
        }
        return delSrcAfter2Hdfs;
    }
    
    public static String hdfsPushUrl() {
        if (hdfsPushUrl == null){
            hdfsPushUrl = get("hdfs_push_url");
        }
        return hdfsPushUrl;
    }
    
    public static String getTempPath() {
        if (tempFolder == null){
            tempFolder = get("hdfs_temp");
            
        }
        
        if (tempFolder == null) {
            tempFolder = System.getProperty("java.io.tmpdir");
        }
        
        return tempFolder;
    }
    
}
