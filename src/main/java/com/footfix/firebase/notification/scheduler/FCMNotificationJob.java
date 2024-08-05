package com.footfix.firebase.notification.scheduler;

import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@Slf4j
public class FCMNotificationJob implements Job {

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    String token = context.getMergedJobDataMap().getString("token");
    String title = context.getMergedJobDataMap().getString("title");
    String body = context.getMergedJobDataMap().getString("body");
    String image = context.getMergedJobDataMap().getString("image");
    String icon = context.getMergedJobDataMap().getString("icon");

    sendNotification(token, title, body, image, icon);
  }

  private void sendNotification(String token, String title, String body, String image, String icon) {

    // 웹 브라우저를 대상으로 하는 푸시 알림을 생성
    WebpushNotification webpushNotification = WebpushNotification.builder()
            .setTitle(title)
            .setBody(body)
            .setIcon(icon) // 아이콘 설정
            .setImage(image)
            .build();

    WebpushConfig webpushConfig = WebpushConfig.builder()
            .setNotification(webpushNotification)
            .build();

    // 주로 모바일 장치(Android 및 iOS)를 대상으로 하는 푸시 알림을 생성
    Notification notification = Notification.builder()
            .setTitle(title)
            .setBody(body)
            .setImage(image)
            .build();

    // 위에서만든 푸시알림으로 메세지 생성
    Message message = Message.builder()
            .setToken(token)
            .setWebpushConfig(webpushConfig)
//            .setNotification(notification)
            .putData("title", title)
            .putData("body", body)
            .putData("image", image)
            .putData("icon", icon)
//            .setAndroidConfig(AndroidConfig.builder()
//                    .setPriority(AndroidConfig.Priority.HIGH)
//                    .build())
            .build();

    try {
      FirebaseMessaging.getInstance().send(message);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
