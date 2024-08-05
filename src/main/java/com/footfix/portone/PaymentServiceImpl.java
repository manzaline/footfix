package com.footfix.portone;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{

  private final IamportClient iamportClient;

  @Override
  public void savePayment(Long userId, OrderInfo orderInfo) throws IamportResponseException, IOException {
    log.info("결재 서비스 시작~!");
    // checkDuplicatePayment(paymentInfoDto); //userId의 DB에 imp_uid나 merchant_uid중복 확인. 프론트단에서 해결?
    int pricePay = orderInfo.getPaymentForPay(); //내야될 돈
    IamportResponse<Payment> iamResponse = iamportClient.paymentByImpUid(orderInfo.getImpUid());
    int pricePaid = iamResponse.getResponse().getAmount().intValue(); //결재한 돈. 금융관련 애플리케이션에선 부동소수점의 오차때문에 BigDecimal을 필수로쓴다
    if(pricePay != pricePaid){
      log.info("결재할 금액과 결재한 금액이 동일하지 않아요~!");
    }else{
      log.info("결재금액 동일~!"+userId);
    }
  }

  @Override
  public void verifyPayment(OrderInfo orderInfo) {

    log.info(orderInfo.getImpUid());
    log.info(orderInfo.getMerchantUid());
    log.info(""+orderInfo.getPaymentForPay());
  }
}
