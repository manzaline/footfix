package com.footfix.firebase.notification.scheduler;

import com.footfix.dao.FootfixDAO;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

@Slf4j
public class FcmJobListener implements JobListener {

  private final FootfixDAO footfixDAO;

  public FcmJobListener(FootfixDAO footfixDAO) {
    this.footfixDAO = footfixDAO;
  }

  @Override
  public String getName() {
    return "getName매서드야~!";
  }

  // 작업 실행되기 전 수행
  @Override
  public void jobToBeExecuted(JobExecutionContext jobExecutionContext) {
    log.info("푸시알림 울리기 전 수행~!");
  }

  // 작업 실행 거부 시
  @Override
  public void jobExecutionVetoed(JobExecutionContext jobExecutionContext) {
    log.info("Job이 실행 취소된 시점에 수행~!");
  }

  // 작업 실행된 후 수행
  @Override
  public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobExecutionException) {

    String jobId = context.getJobDetail().getKey().getName();
    footfixDAO.delDates(jobId);

    // storeDurably()로 유지된 작업을 명시적으로 삭제
    try {
      JobKey jobKey = context.getJobDetail().getKey();
      context.getScheduler().deleteJob(jobKey); // 작업 삭제
    } catch (SchedulerException e) {
      e.printStackTrace();
    }
  }
}
