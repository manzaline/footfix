package com.footfix.portone;

import com.footfix.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class PaymentInfo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "pay_id")
  private Long id;

//  @ManyToOne(fetch = FetchType.LAZY)
//  @JoinColumn(name = "user_id")
//  private User user;

  private int paymentForPaid;
  private PaymentStatus status;
  private String paymentUid;

  @Builder
  public PaymentInfo(int paymentForPaid, PaymentStatus status, String paymentUid){
    this.paymentForPaid = paymentForPaid;
    this.status = status;
    this.paymentUid = paymentUid;
  }
}
