package com.mirai.task;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class SchedulerTask {

    public static void autoRefreshToken() {
        Scheduler scheduler;
        try {
            scheduler = new StdSchedulerFactory().getScheduler();
        } catch(SchedulerException e) {
            throw new RuntimeException(e);
        }
        JobDetail jobDetail = JobBuilder.newJob(RefreshTokenJob.class).build();
        Trigger trigger = TriggerBuilder.newTrigger().startNow()
//                .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(60))
                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(3, 10))
                .build();
        try {
            scheduler.scheduleJob(jobDetail, trigger);
            scheduler.start();
        } catch(SchedulerException e) {
            throw new RuntimeException(e);
        }
    }
}
