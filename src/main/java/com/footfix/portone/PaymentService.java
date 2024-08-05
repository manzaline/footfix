package com.footfix.portone;

import com.siot.IamportRestClient.exception.IamportResponseException;
import org.hibernate.query.Order;

import java.io.IOException;

public interface PaymentService {

  void savePayment(Long userId, OrderInfo orderInfo) throws IamportResponseException, IOException;
  void verifyPayment(OrderInfo orderInfo);
}
