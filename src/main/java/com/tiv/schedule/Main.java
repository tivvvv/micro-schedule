package com.tiv.schedule;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 主启动类，用于演示调度服务的功能。
 * 该类通过 {@link ScheduleService} 安排两个任务，分别每隔1秒和2秒打印一个字符。
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        // 定义时间格式化器，用于格式化当前时间
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        // 创建调度服务实例
        ScheduleService scheduleService = new ScheduleService();

        // 每隔1s打印一个a
        scheduleService.schedule(() -> {
            System.out.printf("%s a%n", LocalDateTime.now().format(dateTimeFormatter));
        }, 1000);

        // 初次执行时需额外等待1s
        Thread.sleep(1000);
        // 每隔2s打印一个b
        scheduleService.schedule(() -> {
            System.out.printf("%s b%n", LocalDateTime.now().format(dateTimeFormatter));
        }, 2000);
    }
}
