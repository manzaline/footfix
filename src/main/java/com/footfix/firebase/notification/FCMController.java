package com.footfix.firebase.notification;

import com.footfix.domain.User;
import com.footfix.security.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class FCMController {

  private final UserRepository userRepository;
  private final FCMTokenRepository fcmTokenRepository;
  private final FCMNotificationServiceImpl notificationServiceImpl;

  @PersistenceContext
  private EntityManager entityManager;

  // 트랜잭션이 필요할 수 있는 모든 메서드에 @Transactional 추가
  @PostMapping("/테스트/{userId}")
  @Transactional
  public ResponseEntity<String> saveNotification(
          @PathVariable Long userId,
          @RequestBody Map<String, String> payload) {

    User user = this.userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
    String fcmToken = payload.get("token");
    String title = payload.get("title");
    String body = payload.get("body");
    Long time = Long.valueOf(payload.get("time"));

    Optional<List<FCMNotificationToken>> existingTokens = this.fcmTokenRepository.findByUserId(userId);
    if(existingTokens.isEmpty() || existingTokens.get().isEmpty()) {
      FCMNotificationToken token = new FCMNotificationToken(user, fcmToken);
      user.addFCMNotificationToken(token);
      this.fcmTokenRepository.save(token);
      log.info("새로운 토큰을 저장했습니다: " + fcmToken);
    } else {
      log.info("토큰이 이미 존재합니다!");
    }

    // user 엔티티가 영속성 컨텍스트 내에서 관리되고 있는지 확인하는 로그 추가
    log.info("User 엔티티의 상태: " + entityManager.contains(user));

    // 관계를 명시적으로 초기화. 아래에 user.getFcmNotificationTokens()를 호출하면 컬렉션을 자동 초기화하므로 생략가능.
    Hibernate.initialize(user.getFcmNotificationTokens());

    List<FCMNotificationToken> fcmTokens = user.getFcmNotificationTokens();

    // FCMNotificationToken 객체의 token 필드만을 추출하여 새로운 List에 저장
    List<String> tokenList = fcmTokens.stream()
            .map(FCMNotificationToken::getToken)
            .collect(Collectors.toList());

//    this.notificationServiceImpl.sendByTokenList(tokenList, title, body, time);
    return ResponseEntity.ok().body(fcmToken);
  }
}


