package com.footfix.controller;

import com.footfix.dto.MessageDTO;
import com.footfix.security.config.auth.PrincipalDetails;
import com.footfix.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TestController {

  @Autowired
  private MessageService messageService;

  @GetMapping("/user")
  @ResponseBody
  public String get(@AuthenticationPrincipal PrincipalDetails principalDetails){
    System.out.println("principalDetails = " + principalDetails.getUser());
    return "환영합니다(principalDetails), " + principalDetails.getUser() + "님.";
  }

  @GetMapping("/mytest")
  public String myTest(Model model){
    return "/mytest";
  }

  @RequestMapping("/message")
  public ResponseEntity<String> message(@RequestBody MessageDTO msg){

    messageService.saveMessage(msg);
    if(msg != null){
      return new ResponseEntity<>("suc", HttpStatus.OK);
    }else{
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }
}
