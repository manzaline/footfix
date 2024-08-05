package com.footfix.firebase.notification.scheduler;

import com.footfix.domain.User;
import com.footfix.firebase.notification.FCMNotificationServiceImpl;
import com.footfix.firebase.notification.FCMNotificationToken;
import com.footfix.firebase.notification.FCMTokenRepository;
import com.footfix.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {

  private final FCMNotificationServiceImpl fcmNotificationService;
  private final UserRepository userRepository;
  private final FCMTokenRepository fcmTokenRepository;
  private final Scheduler scheduler;

  @PostMapping("/saveToken/{userId}")
  public ResponseEntity<String> saveToken(
          @PathVariable Long userId,
          @RequestBody FCMNotificationToken notificationToken) {

    String currentToken = notificationToken.getToken();

    // 토큰이 이미 등록된 토큰인지 확인
    Optional<List<FCMNotificationToken>> existingToken = this.fcmTokenRepository.findByUserId(userId);
    if (existingToken.isPresent() &&
            existingToken.get().stream().anyMatch(token -> token.getToken().equals(currentToken))){
      return ResponseEntity.ok().body("이미 등록된 토큰이 있습니다" + currentToken);
    }

    // 토큰이 등록되어있지 않으면 새로저장
    Optional<User> optionalUser = this.userRepository.findById(userId);
    User user = optionalUser.get();
    FCMNotificationToken fcmNotificationToken = new FCMNotificationToken(user, currentToken); // 토큰 저장
    fcmTokenRepository.save(fcmNotificationToken);
    return ResponseEntity.ok().body("새로운 토큰을 저장했습니다" + currentToken);

    // 사용자가 존재하는지 확인하고  토큰 새로저장
    /*return this.userRepository.findById(userId)
            .map(user -> {
              FCMNotificationToken fcmNotificationToken = new FCMNotificationToken(user, currentToken);
              fcmTokenRepository.save(fcmNotificationToken);
              return ResponseEntity.ok().body("새로운 토큰을 저장했습니다");
            })
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다"));*/
  };

  @PostMapping("/getToken/{userId}")
  public List<String> getToken(@PathVariable Long userId){
    Optional<List<FCMNotificationToken>> existingToken = this.fcmTokenRepository.findByUserId(userId);
    List<String> tokenList = new ArrayList<>();
    existingToken.ifPresent(existingTokens ->
            existingTokens.forEach(token -> tokenList.add(token.getToken())));
    return tokenList;
  }

  @PostMapping("/schedule/{userId}")
  public ResponseEntity<String> scheduleNotification(
          @PathVariable Long userId,
          @RequestBody(required = true) NotificationRequestDto request) throws SchedulerException {

    List<String> tokenList = request.getTokenList();
    tokenList.forEach(token -> {
      fcmNotificationService.scheduleNotification(token, request.getDatas(), request.getImage(), userId);
    });

    return ResponseEntity.ok().body("알림설정 성공!");
  }
}
