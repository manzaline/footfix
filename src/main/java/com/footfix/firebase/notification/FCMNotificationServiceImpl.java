package com.footfix.firebase.notification;

import com.footfix.dao.FootfixDAO;
import com.footfix.firebase.notification.scheduler.FCMNotificationJob;
import com.footfix.firebase.notification.scheduler.FcmJobListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FCMNotificationServiceImpl {

//  private final ScheduledExecutorService schedulerExecutorService = Executors.newScheduledThreadPool(1);

  private final Scheduler scheduler;
  private final FootfixDAO footfixDAO;

  public void scheduleNotification(String token, List<String> datas, String image, Long userId) {

    List<String> myJobIdList = footfixDAO.getJobIdList(userId);
    for (String data : datas){
      String[] splitData = data.split(",");
//      String userId = splitData[0];
      String matchId = splitData[1];
      String jobId = userId + "_" + matchId;

      if(myJobIdList.contains(jobId)){
        continue; // jobId가 포함되어 있으면 다음 데이터로 넘어간다. forEach는 반복문이 아닌 함수이기때문이다.
      }

      String timeString = splitData[2];
//      String timeString = "2024-07-16 15:18";
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
      LocalDateTime localDateTime = LocalDateTime.parse(timeString, formatter);

      // LocalDateTime을 Date로 변환
      Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

      String title = splitData[3] + " vs " + splitData[4]; // home팀 vs away팀
      String body = "경기가 곧 시작됩니다!";
      String icon = "http://localhost:8080/images/쭈루리.jpg";
//      String jobId = UUID.randomUUID()+"; " + timeString + title;

      JobDetail jobDetail = JobBuilder.newJob(FCMNotificationJob.class)
              .withIdentity(jobId, "FOOTFIX")
              .usingJobData("token", token)
              .usingJobData("title", title)
              .usingJobData("body", body)
              .usingJobData("image", image)
              .usingJobData("icon", icon)
              .usingJobData("time", timeString)
//              .storeDurably() // 트리거 없이도 작업을 유지해야하는 경우 추가
              .build();

      String triggerId = jobId + "_trigger";
      Trigger trigger = TriggerBuilder.newTrigger()
              .withIdentity(triggerId, "FOOTFIX")
              .startAt(date)
              .build();

      try{
        JobKey jobKey = new JobKey(jobId, "FOOTFIX");
        if (scheduler.checkExists(jobKey)){
          scheduler.deleteJob(jobKey);
        }
        FcmJobListener fcmJobListener = new FcmJobListener(footfixDAO);
        scheduler.getListenerManager().addJobListener(fcmJobListener); // 알림실행 전,후,취소시점에서 작업을 수행하기위해 추가
        scheduler.scheduleJob(jobDetail, trigger);
      } catch (SchedulerException e) {
        e.printStackTrace();
      }
    };
  }

  public void cancelNotification(String jobId) throws SchedulerException {
    footfixDAO.delDates(jobId);
    JobKey jobKey = new JobKey(jobId, "FOOTFIX");
    scheduler.deleteJob(jobKey);
  }


  // 알림 보내기
  /*public void sendByTokenList(List<String> tokenList, String title, String body, Long time) {

    Runnable task = () -> {
      // 메시지 만들기
      List<Message> messages = tokenList.stream().map(token -> Message.builder()
              .putData("time", LocalDateTime.now().toString())
              .putData("title", title)
              .putData("body", body)
              .putData("image", "http://localhost:8080/images/쭈루리.jpg")
              .setToken(token)
              .build()).collect(Collectors.toList());

      // 요청에 대한 응답을 받을 response
      BatchResponse response;

      try {

        // 알림 발송
        response = FirebaseMessaging.getInstance().sendAll(messages);

        // 요청에 대한 응답 처리
        if (response.getFailureCount() > 0) {
          List<SendResponse> responses = response.getResponses();
          List<String> failedTokens = new ArrayList<>();

          for (int i = 0; i < responses.size(); i++) {
            if (!responses.get(i).isSuccessful()) {
              failedTokens.add(tokenList.get(i));
            }
          }
          log.error("List of tokens are not valid FCM token : " + failedTokens);
        }
      } catch (FirebaseMessagingException e) {
        log.error("cannot send to memberList push message. error info : {}", e.getMessage());
      }
    };

    schedulerExecutorService.schedule(task, time, TimeUnit.MILLISECONDS);
  }*/
}

