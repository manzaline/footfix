package com.footfix.firebase.notification;

import com.footfix.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FCMNotificationToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "fcm_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  private String token;

  public FCMNotificationToken(User user, String token) {
    this.user = user;
    this.token = token;
  }

//  @Override
//  public String toString() {
//    return "FCMNotificationToken{" +
//            "id=" + id +
//            ", token='" + token + '\'' +
//            '}';
//  }
}
