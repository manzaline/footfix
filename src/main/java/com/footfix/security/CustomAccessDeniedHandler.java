package com.footfix.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  //403 접근 제한이 된 경우에 다양한 처리를 하고 싶다면 직접 AccessDeniedHandler 인터페이스를 구현 상속받는다
  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
                     AccessDeniedException accessDeniedException) throws IOException, ServletException {

    System.out.println("\n"+accessDeniedException.getMessage());
    System.out.println("CustomAccessDeniedHandler.handle 호출~!");
    System.out.println("접근권한이 없습니다!! accessDenied페이지로 리다이렉트 중......\n");

    response.sendRedirect("/accessError");
  }
}
