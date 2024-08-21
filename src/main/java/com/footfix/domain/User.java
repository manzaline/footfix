package com.footfix.domain;

import com.footfix.firebase.notification.FCMNotificationToken;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "users")
@Data
@NoArgsConstructor
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String username;
  @Column(nullable = false)
  private String password;
  @Column(nullable = false, unique = true)
  private String email;
  @Column(nullable = false)
  private String role;

  private String provider;
  private String providerId;

  @CreationTimestamp
  private Timestamp createDate;

  // mappedBy 속성은 FCMNotificationToken 클래스의 user 필드를 설정
  @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, orphanRemoval = true)
  private List<FCMNotificationToken> fcmNotificationTokens = new ArrayList<>();

  public void addFCMNotificationToken(FCMNotificationToken token) {
    fcmNotificationTokens.add(token);
    token.setUser(this);
  }

  @Builder
  public User(String username, String password, String email, String role, String provider, String providerId) {
    this.username = username;
    this.password = password;
    this.email = email;
    this.role = role;
    this.provider = provider;
    this.providerId = providerId;
  }
}
