/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocapture;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.LinkedList;
import org.apache.log4j.Logger;

/**
 *
 * @author haind25
 */
public class ScreenLogger implements Runnable {

    private final Logger log = Logger.getLogger(ScreenLogger.class);

    private final LinkedList<String> queue = new LinkedList<String>();
    private final Object lock = new Object();

    private boolean running = false;

    private boolean init;

    private javax.swing.JTextArea txaLog;

    private int countMessage = 0;

    public ScreenLogger(javax.swing.JTextArea txaLog) {
        this.txaLog = txaLog;
    }

    public void enqueue(String log) {
        synchronized (lock) {
            queue.add(log);
        }
    }

    public void start() {
        running = true;

        if (init) {
            return;
        }
        Thread t = new Thread(this);
        t.setName("thread-ScreenLogger");
        t.start();
        init = true;

    }

    public void stop() {
        running = false;
    }

    private String dequeue() {
        synchronized (lock) {
            if (queue.isEmpty()) {
                return null;
            }

            return queue.poll();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (!running) {
                    Thread.sleep(1000);//sleep 15s
                    return;
                }

                String logString = dequeue();
                if (logString != null) {
                    if (countMessage > 2000) {
                        countMessage = 0;
                        txaLog.setText(logString);
                    } else {
                        countMessage++;
                        txaLog.append(logString + "\n");
                    }
                    
                    log.info("nothing to run");
                } else {
                    log.debug("nothing to run");
                }

                Thread.sleep(100);//sleep 15s
//                Thread.yield();
            } catch (Exception x) {
            }
        }
    }

}
