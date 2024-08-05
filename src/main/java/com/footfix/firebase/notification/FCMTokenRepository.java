package com.footfix.firebase.notification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface FCMTokenRepository extends JpaRepository<FCMNotificationToken, Long> {

  Optional<List<FCMNotificationToken>> findByToken(String fcmToken);
  Optional<List<FCMNotificationToken>> findByUserId(Long userId);

}
