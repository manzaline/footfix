package com.footfix.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sample")
public class SampleController {

  @GetMapping("/all")
  public void all() { System.out.println("모든 사용자 접근 가능~!~"); }

  @GetMapping("/member")
//  @MemberAuthorize
  public void doMember() {
    System.out.println("로그인한 사용자군용~!");
  }

  @GetMapping("/manager")
//  @ManagerAuthorize
  public void doManager() { System.out.println("관리자님 어서오세영~!"); }

  @GetMapping("/admin")
//  @AdminAuthorize
  public void doAdmin() {
    System.out.println("운영자님 어서요세영~");
  }
}
