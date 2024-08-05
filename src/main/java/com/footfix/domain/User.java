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

@Data
@NoArgsConstructor
@Entity
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String username;
  private String password;
  private String email;
  private String role;

  private String provider;
  private String providerId;

  @CreationTimestamp
  private Timestamp createDate;

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

//  @Override
//  public String toString() {
//    return "User{" +
//            "id=" + id +
//            ", username='" + username + '\'' +
//            ", email='" + email + '\'' +
//            ", role='" + role + '\'' +
//            ", provider='" + provider + '\'' +
//            ", providerId='" + providerId + '\'' +
//            ", createDate=" + createDate +
//            '}';
//  }
}
