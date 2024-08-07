package com.footfix.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication auth) throws IOException, ServletException {

    System.out.printf("\n[%s] ===>> 로그인 성공~! CustomLoginSuccessHandler 호출~!", LocalDateTime.now());

    List<String> roles = new ArrayList<>();

    auth.getAuthorities().forEach(authority -> roles.add(authority.getAuthority()));

//    auth.getAuthorities().forEach(authority ->
//            roles.addAll(Arrays.asList(authority.getAuthority().split(","))));

    HttpSession session = request.getSession(false);
    session.setMaxInactiveInterval(3600); // 세션 타임아웃(클라이언트의 요청이 없을때 세션을 유지하는 시간), 초단위
    session.setAttribute("testKey","testValue");

    System.out.println("\n세션의 getId(): "+session.getId()); // JSESSIONID의 쿠키값

    System.out.println("\nROLES : " + roles + "환영합니다~! *^^*");

    /**
     *  위 람다식을 풀어쓰면
     *  for (GrantedAuthority authority : auth.getAuthorities()) {
     *        roleNames.add(authority.getAuthority());
     *      }
     *  가 된다.
     *
     *  Authentication(인터페이스)의 getAuthorities()매서드는
     *  저장된 권한들을 Collection<GrantedAuthority>로 반환하는 클래스다
     *  GrantedAuthority(함수형 인터페이스)의 getAuthority() 매서드는 가지고있는 권한을 문자열로 반환하고
     *  이것을 List roleNames에 저장한다.
     *
     *  Authentication은 사용자 신원과 권한을 모두담고 있고
     *  GrantedAuthority는 권한만 담고있다. 따라서 Authentication이 상위개념인 셈이다.
     */

    if(roles.contains("ROLE_ADMIN")){
      response.sendRedirect("/index");
    }

    if(roles.contains("ROLE_MANAGER") || roles.contains("ROLE_MEMBER")){
      response.sendRedirect("/index");
    }
  }
}










