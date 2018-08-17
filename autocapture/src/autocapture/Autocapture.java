/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocapture;

import java.io.File;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author haind25
 */
public class Autocapture {
    
    public static final String HOME_DIR = "config/";
    protected static String CONFIG_APP = "config.properties";
    protected static String CONFIG_LOG4J = "log4j.properties";
    protected static String HOME_CONFIG = "etc/";
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        String home = System.getProperty("home");
        if (home != null && !home.isEmpty()) {
            if (!home.endsWith(File.separator)){
                home = home + File.separator;
            }
            HOME_CONFIG = home;
        }

        PropertyConfigurator.configure(HOME_CONFIG + CONFIG_LOG4J);
        GlobalConfig.config(HOME_CONFIG + CONFIG_APP);
        AutoCaptureMain abc = new AutoCaptureMain();
        abc.setVisible(true);
    }
    
    

}
