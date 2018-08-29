package autocapture;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.log4j.Logger;

/**
 *
 * @author haizpt
 */
public class TaskExcutor implements Runnable{
    private static final Logger log = Logger.getLogger(TaskExcutor.class);
    private final static int DEFAULT_SLEEP = 1 * 60 * 1000; // 1 minutes
    private final static int MIN_NEVER_YIELD = 2;
    
    private final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    private final SimpleDateFormat formatTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    
    private static final String SEQUENCE_TEMPLATE = "0000000"; 
    
    private final String name;
    private int indexThread = -1;  
    
    public  TaskExcutor(int indexThread) {
        this.indexThread = indexThread;
        this.name = "TaskExcutor-" + indexThread;
    }
    
    public void start() {
        Thread t = new Thread(this);
        t.setName(name + "_" + System.currentTimeMillis());
        t.setDaemon(true);
        t.start();
    }
    
    private String getName() {
        return name;
    }
    
    @Override
     public void run() {
        while (true) {
            try {
                Task task = TaskQueue.getInstance().dequeue();
                if (task != null) {
                    process(task);
                }
                
                Thread.sleep(DEFAULT_SLEEP);
                if (indexThread >= MIN_NEVER_YIELD) {
                    Thread.yield();
                }
            } catch (Exception ex) {
                log.error(getName() + "error when excute: " , ex);
            }
        }
    }
  
    public String getPart(int part) {
        String sequence = "";
        sequence += part;
        int length = sequence.length();
        String sequenceTemplate = SEQUENCE_TEMPLATE;
        if (length <= SEQUENCE_TEMPLATE.length()) {
            sequenceTemplate = sequenceTemplate.substring(length, SEQUENCE_TEMPLATE.length());
        }
        
        sequence = sequenceTemplate + sequence;
            
        return sequence;
    }
    
    private static final String CONTEXT_FILE_PART="${PART}";
    private static final String CONTEXT_FILE_TOKEN="${TOKEN}";
    private static final String CONTEXT_FILE_QUERYITEM="${QUERYID}";
    
    public static void process(Task task) {
        long startTime = System.currentTimeMillis();
        ScreenLogger logger = AutoCaptureMain.getLogger();
        
        SimpleDateFormat sm = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
        String imgName = sm.format(new Date(System.currentTimeMillis()));
        logger.enqueue("===================================================");
        logger.enqueue("capture start: " + imgName);
        
        String file = capture();
        long endTime = System.currentTimeMillis();
        
        logger.enqueue("capture end: " + imgName + " in (ms) " + (endTime - startTime));
        
        if (file != null && AutoCaptureMain.enableEmail) {
            logger.enqueue("email start: " + imgName);
            sendEmail(file);
            logger.enqueue("email end: " + imgName + " in (ms) " + ( System.currentTimeMillis() - startTime));
        }
        endTime = System.currentTimeMillis();
        logger.enqueue("finish: " + imgName + " in (ms) " + (endTime - startTime));
        logger.enqueue("===================================================");
    }
    
    
    private static String capture() {
        String fileName = null;
        try {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Rectangle screenRectangle = new Rectangle(screenSize);
            Robot robot = new Robot();
            BufferedImage image = robot.createScreenCapture(screenRectangle);
            SimpleDateFormat sm = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
            String imgName = sm.format(new Date(System.currentTimeMillis()));
            String realName = AutoCaptureMain.directory;
            if (!realName.endsWith("/") ){
                realName = realName + "/";
            }
            fileName = realName + "/"+ imgName + ".png";
            
            ImageIO.write(image, "png", new File(fileName));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        
        return fileName;
    }
    
    public synchronized static void sendEmail (String file) {
        try {
            // TODO code application logic here
            Properties prop = new Properties();
            prop.put("mail.smtp.auth", true);
            prop.put("mail.smtp.starttls.enable", "true");
            prop.put("mail.smtp.host", AutoCaptureMain.config_email_smtp);
            prop.put("mail.smtp.port", AutoCaptureMain.config_email_port);
           
            prop.put("mail.smtp.timeout", "120000");
            prop.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
            prop.put("mail.smtp.debug", "true");
            
            prop.put("mail.smtp.ssl.trust", "*");
//            prop.put("mail.smtp.ssl.trust", "smtp.mailtrap.io");
            
            Session session = Session.getInstance(prop, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(AutoCaptureMain.config_email_user.trim(), AutoCaptureMain.config_email_password.trim());
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(AutoCaptureMain.from));

//            String to = "haind25@viettel.com.vn, namdh1@viettel.com.vn,hungdd3@viettel.com.vn";
            String to = AutoCaptureMain.repcipient;
            InternetAddress[] adrss = InternetAddress.parse(to, true);

            message.setRecipients(
                    Message.RecipientType.TO, adrss);
            message.setRecipients(Message.RecipientType.TO, adrss);
            message.setSubject(AutoCaptureMain.subject);

            String msg = AutoCaptureMain.message;

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(msg, "text/html");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            message.setContent(multipart);
            
            if (file != null) {
                MimeBodyPart attachmentBodyPart = new MimeBodyPart();
                attachmentBodyPart.attachFile(new File(file));
                multipart.addBodyPart(attachmentBodyPart);
            }
            
            Transport.send(message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
