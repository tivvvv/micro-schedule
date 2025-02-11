package com.tiv.schedule;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.LockSupport;

/**
 * 调度服务类, 用于任务的计划与执行. 
 * 该类提供了一个简单的调度机制, 允许用户安排任务在指定的延迟后执行. 
 * 任务由内部的触发器线程监控, 并通过线程池执行. 
 */
public class ScheduleService {

    /**
     * 线程池的线程数量, 默认为3个线程. 
     */
    private final int threadsNum = 3;

    // 触发器对象, 用于管理任务的调度
    Trigger trigger = new Trigger();

    // 固定大小的线程池, 用于执行调度的任务
    ExecutorService executorService = Executors.newFixedThreadPool(threadsNum);

    /**
     * 安排一个任务在指定的延迟后执行. 
     *
     * @param task  要执行的任务
     * @param delay 延迟的时间(毫秒)
     */
    void schedule(Runnable task, long delay) {
        // 创建一个Job对象, 包含任务, 启动时间和延迟
        Job job = new Job(task, System.currentTimeMillis() + delay, delay);
        // 将任务加入到触发器的队列中
        trigger.queue.offer(job);
        // 唤醒等待中的触发器线程
        trigger.wakeUp();
    }

    /**
     * 触发器类, 负责任务的调度与触发. 
     * 触发器使用一个优先级阻塞队列来存储待执行的任务, 并通过一个单独的线程来监控和执行这些任务. 
     */
    class Trigger {
        // 优先级阻塞队列, 用于存储待执行的任务
        PriorityBlockingQueue<Job> queue = new PriorityBlockingQueue<>();

        // 触发器线程, 负责监控和执行任务
        Thread thread = new Thread(() -> {
            while (true) {
                // 当队列为空时, 线程进入等待状态, 直到有新任务加入或被唤醒
                while (queue.isEmpty()) {
                    LockSupport.park();
                }
                // 获取队列中优先级最高的任务
                Job latestJob = queue.peek();
                // 如果任务的启动时间已到, 则执行任务
                if (latestJob.getStartTime() < System.currentTimeMillis()) {
                    latestJob = queue.poll();
                    executorService.execute(latestJob.getTask());
                    // 重新计算下一次执行时间, 并将任务重新加入队列(如果需要周期性执行)
                    Job nextJob = new Job(latestJob.getTask(), System.currentTimeMillis() + latestJob.getDelay(), latestJob.getDelay());
                    queue.offer(nextJob);
                } else {
                    // 如果任务的启动时间未到, 则线程等待直到启动时间
                    LockSupport.parkUntil(latestJob.getStartTime());
                }
            }
        });

        {
            // 启动触发器线程
            thread.start();
            System.out.println("触发器启动");
        }

        /**
         * 唤醒触发器线程, 使其检查并可能执行任务. 
         */
        void wakeUp() {
            LockSupport.unpark(thread);
        }
    }
}
