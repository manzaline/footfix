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
public class OrderInfo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "order_id")
  private int orderId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "pay_id")
  private PaymentInfo paymentInfo;

  private String merchantUid;
  private String impUid;
  private int paymentForPay;
  private String productName;

  @Builder
  public OrderInfo(String impUid, int paymentForPay, String productName, String merchantUid, User user, PaymentInfo paymentInfo){
    this.impUid = impUid;
    this.paymentForPay = paymentForPay;
    this.productName = productName;
    this.merchantUid = merchantUid;
    this.user = user;
    this.paymentInfo = paymentInfo;
  }
}
