package com.tiv.schedule;

/**
 * 自定义任务类, 用于表示一个可调度的任务. 
 * 每个任务包含一个要执行的操作(Runnable), 计划的启动时间以及执行间隔(延迟). 
 * 该类实现了{@link Comparable}接口, 以便根据任务的启动时间进行排序. 
 */
public class Job implements Comparable<Job> {

    /**
     * 需要执行的任务
     */
    private Runnable task;

    /**
     * 任务开始时间戳(ms)
     */
    private Long startTime;

    /**
     * 下一次任务执行的间隔时间(ms)
     */
    private Long delay;

    public Job(Runnable task, Long startTime, Long delay) {
        this.task = task;
        this.startTime = startTime;
        this.delay = delay;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Runnable getTask() {
        return task;
    }

    public void setTask(Runnable task) {
        this.task = task;
    }

    public Long getDelay() {
        return delay;
    }

    public void setDelay(Long delay) {
        this.delay = delay;
    }

    @Override
    public int compareTo(Job o) {
        return Long.compare(this.startTime, o.startTime);
    }
}
