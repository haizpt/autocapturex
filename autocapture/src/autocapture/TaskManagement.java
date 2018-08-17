/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocapture;

import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author haind25
 */
public class TaskManagement implements Runnable {

    private final Logger log = Logger.getLogger(TaskManagement.class);

    private final List<Command> commands;
    private final Object lock = new Object();

    private boolean running = false;

    private boolean init;
    private String line;

    public TaskManagement() {
        commands = new LinkedList<>();
        init = false;

    }

    public synchronized void setScheduler(String line) {
        this.line = line.trim();
        loadCommand();
    }

    private boolean loadCommand() {
        synchronized (lock) {
            commands.clear();
            String[] args = line.split(" ");
            if (args == null || args.length < 5) {
                log.warn("line error, length < 5, abort =>" + line);
                return false;
            }
            Command c = new Command(args);
            c.setTextPlain(line);
            commands.add(c);
            log.debug("command size: " + commands.size());
            return true;
        }
    }

    public void start() {
        running = true;

        if (init) {
            loadCommand();
            return;
        }
        Thread t = new Thread(this);
        t.setName("thread-TaskManagement");
        t.start();
        init = true;

    }

    public void stop() {
        synchronized (lock) {
            commands.clear();
            running = false;
        }
    }

    private synchronized List<Command> getRunCommand() {
         synchronized (lock) {
             if (commands.isEmpty()) {
                 return null;
             }
            List<Command> runCommands = new LinkedList<>();
            for (Command c : commands) {
                if (c.isRuntime()) {
                    runCommands.add(c);
                }
            }
            return runCommands;
         }
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (!running) {
                    log.info("not running");
                    Thread.sleep(15000);//sleep 15s
                    continue;
                }

                List<Command> runCommands = getRunCommand();
                if (!runCommands.isEmpty()) {
                    for (Command command : runCommands) {
                        TaskQueue.getInstance().enqueue(command.getCommand());
                    }
                } else {
                    log.info("nothing to run");
                }

                Thread.sleep(15000);//sleep 15s
//                Thread.yield();
            } catch (Exception x) {
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=======================================================\n");
        sb.append("=================").append(this.getClass().getSimpleName())
                .append("=========================\n");
        sb.append("======active command======================\n");
        for (Command command : commands) {
            sb.append(command.toString()).append("\n");
        }
        sb.append("=======================================================\n");
        sb.append("=======================================================\n");

        return sb.toString();
    }

}
