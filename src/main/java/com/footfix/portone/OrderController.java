package com.footfix.portone;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  @GetMapping("/order")
  public String order(

          @RequestParam(name = "message", required = false) String message,
          @RequestParam(name = "orderUid", required = false) String orderUid,
          @RequestParam(name = "userId", required = true) String userId,
          Model model){
    model.addAttribute("message",message);
    model.addAttribute("orderUid",orderUid);
    model.addAttribute("userId",userId);

    return "/order";
  }

  @PostMapping("/order")
  public String saveOrder() {

//    OrderInfo orderInfo = OrderService.autoOrder(User);
    String message = "주문 실패 ㅜㅜ";
    log.info(message);
    return "";
  }
}
