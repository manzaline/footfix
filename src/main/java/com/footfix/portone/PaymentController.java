package com.footfix.portone;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class PaymentController {

  private final PaymentService paymentService;

  public PaymentController(PaymentService paymentService) {
    this.paymentService = paymentService;
  }

  //결재성공시 검증
  @PostMapping("/test/payment/success/{id}")
  public ResponseEntity<?> validPayment(
          @RequestBody OrderInfo orderInfo,
          @PathVariable(value = "id") final Long userId) {

    log.info("결재 컨트롤러 시작~!");
    try {
      paymentService.savePayment(userId, orderInfo);
      paymentService.verifyPayment(orderInfo);
      return ResponseEntity.ok().build();
    }catch (Exception e){
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

}
