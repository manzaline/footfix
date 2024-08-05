package com.footfix.firebase.firestoredb;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

  @Value("${fcm.key.path}")
  private String fcmKeyPath;

  // Admin SDK를 초기화
  @PostConstruct
  public void init() {
    try {
      if (FirebaseApp.getApps().isEmpty()) {
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(new ClassPathResource("footfix-f417c-firebase-adminsdk-7fwsl-8e0ba158e8.json").getInputStream()))
                .build();

        FirebaseApp.initializeApp(options);
      }
    } catch (IOException e) { e.printStackTrace(); }
  }

//  @PostConstruct
//  public void init(){
//    try{
//      FileInputStream serviceAccount = new FileInputStream(fcmKeyPath);
//      FirebaseOptions options = new FirebaseOptions.Builder()
//              .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//              .build();
//      FirebaseApp.initializeApp(options);
//    }catch (Exception e){
//      e.printStackTrace();
//    }
//  }
}
