package com.footfix.security.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//@Component
public class RememberMeCookieSetMaxAgeFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
          throws ServletException, IOException {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.isAuthenticated()) {
      // 세션에서 필터 실행 여부 확인
      Boolean filterExecuted = (Boolean) request.getSession().getAttribute("rememberMeFilterExecuted");
      if (filterExecuted == null || !filterExecuted) {
        System.out.println("SetMaxAge실행 ~!");
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
          for (Cookie cookie : cookies) {
            if ("footfix-remember-me".equals(cookie.getName())) {
              System.out.println("\nSetMaxAge 쿠키만료시간이 적용될 쿠키이름: " + cookie.getName());
              cookie.setMaxAge(10); // 쿠키 만료 시간 설정
              cookie.setPath("/");
              response.addCookie(cookie);
              System.out.println("쿠키이름: "+cookie.getName()+", 쿠키만료시간: "+cookie.getMaxAge());
            }
          }
        }
        // 필터가 실행되었음을 세션에 저장
        request.getSession().setAttribute("rememberMeFilterExecuted", true);
      }
    }

    filterChain.doFilter(request, response);
  }


//  @Override
//  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//          throws ServletException, IOException {
//
//    Cookie[] cookies = request.getCookies();
//    if (cookies != null) {
//      for (Cookie cookie : cookies) {
//        if ("footfix-remember-me".equals(cookie.getName())) {
//          System.out.println("찾으려는 쿠키: "+cookie.getName());
//          // 쿠키의 Max-Age가 0이거나 만료된 경우
//          if (cookie.getMaxAge() <= 0) {
//            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//
//            // 쿠키가 만료된 경우 로그아웃 처리
//            if (auth != null) {
//              System.out.printf("\n만료처리 테스트1 %s %s", cookie.getName(), cookie.getMaxAge());
//              new SecurityContextLogoutHandler().logout(request, response, auth);
//            }
//
//            // 만료된 쿠키 삭제
//            cookie.setMaxAge(0);
//            response.addCookie(cookie);
//            response.sendRedirect("/customLogin?logout=true");
//            System.out.printf("\n만료처리 테스트2 %s %s초", cookie.getName(), cookie.getMaxAge());
//            return;
//          }
//        }
//      }
//    }
//    filterChain.doFilter(request, response);
//  }
}


