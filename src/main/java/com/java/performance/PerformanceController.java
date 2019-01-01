package com.java.performance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class PerformanceController {

    private static final int NUMBER_OF_OBJECTS_TO_CREATE = 10000;

    @Autowired
    private TaskExecutor taskExecutor;

    /**
     * 1. Normal application behaviour, just create objects that can be garbage collected
     * @param timeToWait the time to wait between runs of creating objects (in ms)
     */
    @RequestMapping("/startcreatingobjects/{timeToWait}")
    public void startCreatingObjects(@PathVariable int timeToWait) {

        taskExecutor.execute(() -> {
            Map m = new HashMap();
            while (true) {
                for (int i = 0; i < NUMBER_OF_OBJECTS_TO_CREATE; i++) {
                    m.put(new Key(i), new MyNormalObject());
                }

                System.out.println("Created objects");

                // Wait for the given amount of time
                try {
                    Thread.sleep(timeToWait);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 2. Memory leak behaviour, create objects which cannot be garbage collected
     * @param timeToWait the time to wait between runs of creating objects (in ms)
     */
    @RequestMapping("/startleaking/{timeToWait}")
    public void startLeaking(@PathVariable int timeToWait) {

        taskExecutor.execute(() -> {
            Map m = new HashMap();
            while (true) {
                for (int i = 0; i < NUMBER_OF_OBJECTS_TO_CREATE; i++) {
                    if (!m.containsKey(i)) {
                        m.put(new FaultyKey(i), new MyPerformance());
                    }
                }

                System.out.println("Created leaking objects");

                // Wait for the given amount of time
                try {
                    Thread.sleep(timeToWait);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 3. Deadlock behaviour, two threads waiting for each other
     * Thread 1 acquires a lock on object 1 and inside this lock, it needs
     * to acquire a lock on object 2.
     * Thread 2 acquires a lock on object 2 and inside this lock, it needs
     * to acquire a lock on object 1.
     */
    @RequestMapping("/createdeadlock")
    public void createDeadlock() {

        Object lock1 = new Object();
        Object lock2 = new Object();

        taskExecutor.execute(() -> {
            synchronized (lock1) {
                String threadName = Thread.currentThread().getName();
                System.out.println(threadName + " acquired lock on Lock1");

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                synchronized(lock2) {
                    System.out.println(threadName + " acquired lock on Lock2");
                }
            }
        });

        taskExecutor.execute(() -> {
            synchronized (lock2) {
                String threadName = Thread.currentThread().getName();
                System.out.println(threadName + " acquired lock on Lock2");

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                synchronized(lock1) {
                    System.out.println(threadName + " acquired lock on Lock1");
                }
            }
        });
    }

    /**
     * An url to test whether a peformance issue occurs
     * @return A string printing a random number in order to verify whether the return message changed.
     */
    @RequestMapping("/testperformance")
    public String testPerformance() {
        return "Returned " + Math.random();
    }
}
