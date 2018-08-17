/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package autocapture;



import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author haizpt
 */
public class TaskQueue {
    
    private final Map<String, Task> queue = new LinkedHashMap<String, Task>();

    private final Object obj = new Object();
    private static TaskQueue _instance = null;
    
    private final static Logger log = Logger.getLogger(TaskQueue.class);
    private boolean isRunining ;
    
    private TaskQueue() {
        isRunining = true;
    }

    public synchronized static TaskQueue getInstance() {
        if (_instance == null) {
            _instance = new TaskQueue();
            int excutorLength = GlobalConfig.getExcutorLength();
            log.info("length of excutor: " + excutorLength);
            for (int i = 0; i < excutorLength; i++) {
                TaskExcutor excutor = new TaskExcutor(i);
                excutor.start();
            }
        }
        
        return _instance;
    }
   
    public void enqueue(String cmd) {
        synchronized (obj) {
            if(!isRunining) {
                return;
            }
            Task task = new Task(cmd);
            queue.put(task.getId(), task);
            log.info("enqueue queue: " + cmd + " , now");
            log.info("size of queue: " + queue.size() );
        }
    }
    

    public  Task dequeue() {
        synchronized (obj) {
            if (!isRunining) {
                return null;
            }
            
            if (queue.isEmpty()) {
                return null;
            }
            Task itemName = null;
            for (Map.Entry<String, Task> entry : queue.entrySet()) {
                Task task = entry.getValue();
                if (System.currentTimeMillis() > task.getRunTime()) {
                    itemName = task;
                    break;
                }      
            }
            if (itemName != null) {
                queue.remove(itemName.getId());
            }
            return itemName;
        }
    }
    
    public void start() {
        isRunining = true;
    }
    
    public void stop() {
        isRunining = false;
    }
   
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=======================================================\n");
        sb.append("=================").append(this.getClass().getSimpleName())
                .append("=========================\n");
        sb.append("Task in queue: \n");
         
        for (Map.Entry<String, Task> entry : queue.entrySet()) {
                Task task = entry.getValue();
                sb.append(task.toString()).append("\n");
        }
        sb.append("=======================================================\n");
        sb.append("=======================================================\n");
        
        return sb.toString();
    }
}

