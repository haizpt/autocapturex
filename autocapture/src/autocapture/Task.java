/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package autocapture;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @author haind25
 */
public class Task {
    public final static long TIME_WAIT = 5 * 60 * 1000 ; //5 minutes
    public final static long MAX_TIME_WAIT = 10 ;
    
    private String cmd;
    private long runTime;
    private Date prdId;
    private String id;
//    private SimpleDateFormat smp = new SimpleDateFormat("yyyyMMddHHmmss");
    private SimpleDateFormat smp = new SimpleDateFormat("yyyyMMdd");
    private int time = 1;
    
    public Task(String cmd, long runTime, Date prdId) {
        this.cmd = cmd;
        this.runTime = runTime;
        this.prdId = prdId;
        UUID uuid = UUID.randomUUID();
        id = uuid.toString();
    }

    public Task(String cmd, long runTime) {
        this(cmd, runTime, null);
    }

    public Task(String cmd) {
        this(cmd, System.currentTimeMillis(), null);
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public long getRunTime() {
        return runTime;
    }

    public void setRunTime(long runTime) {
        this.runTime = runTime;
    }

    public String getId() {
        return id;
    }

    public Date getPrdId() {
        return prdId;
    }
    
    public String getPrdIdToString() {
        if (prdId == null) {
            return null;
        }
        String tmPrd = smp.format(prdId);
//        String prd = "to_date('"+tmPrd+"','yyyyMMddHHmmss')";
         String prd = "to_date('"+tmPrd+"','yyyyMMdd')";
        return prd;
    }
    
    public boolean hasNextTime() {
        if (time > MAX_TIME_WAIT) {
            return false;
        }
        
        time++ ;
        for (int i = 0; i < time; i++) {
           runTime += TIME_WAIT;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return cmd + "|" + (new Date(runTime)).toString();
    }
    
    
}
