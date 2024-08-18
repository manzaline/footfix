package com.footfix.firebase.notification;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Configuration
public class FCMConfig {

  @Value("${fcm.key.path}")
  private String fcmKeyPath;

  @Bean
  FirebaseMessaging firebaseMessaging() throws IOException {
//    ClassPathResource resource = new ClassPathResource("배포경로 firebase/파일이름.java");
//    InputStream refreshToken = resource.getInputStream();

//    FileInputStream refreshToken = new FileInputStream(fcmKeyPath); // 로컬에서 개발할때
    InputStream refreshToken = getClass().getClassLoader().getResourceAsStream("footfix-f417c-firebase-adminsdk-7fwsl-8e0ba158e8.json"); // jar파일로 실행할때는 resources경로가 사라진다.

    FirebaseApp firebaseApp = null;
    List<FirebaseApp> firebaseAppList = FirebaseApp.getApps();

    //'firebaseapp name [deafault] already exists!'라는 firebase initializeApp을 중복실행하여 발생하는 오류 방지
    if(firebaseAppList != null && !firebaseAppList.isEmpty()) {
      for (FirebaseApp app : firebaseAppList) {
        if (app.getName().equals(FirebaseApp.DEFAULT_APP_NAME)) {
          firebaseApp = app;
        }
      }
    } else {
      FirebaseOptions options = FirebaseOptions.builder()
              .setCredentials(GoogleCredentials.fromStream(refreshToken))
              .build();

      firebaseApp = FirebaseApp.initializeApp(options);
    }
    return FirebaseMessaging.getInstance(firebaseApp);
  }
}
