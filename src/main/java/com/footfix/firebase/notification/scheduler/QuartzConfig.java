package com.footfix.firebase.notification.scheduler;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

// Quartz 스케줄러 설정
@Configuration
public class QuartzConfig {

  @Bean
  public SchedulerFactoryBean schedulerFactoryBean() {
    return new SchedulerFactoryBean();
  }

  @Bean
  public Scheduler scheduler(SchedulerFactoryBean factoryBean) throws SchedulerException{
    Scheduler scheduler = factoryBean.getScheduler();
    scheduler.start();
    return scheduler;
  }

  @Bean
  public JobDetail jobDetail() {
    return JobBuilder.newJob(FCMNotificationJob.class)
            .withIdentity("fcmNotificationJob")
            .storeDurably()
            .build();
  }

  @Bean
  public Trigger trigger(JobDetail jobDetail){
    return TriggerBuilder.newTrigger()
            .forJob(jobDetail)
            .withIdentity("fcmNotificationTrigger")
            .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
            .build();
  }
}
