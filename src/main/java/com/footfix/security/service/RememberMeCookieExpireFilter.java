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
public class RememberMeCookieExpireFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
          throws ServletException, IOException {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if ("footfix-remember-me".equals(cookie.getName())) {
          // 쿠키의 Max-Age가 0이거나 만료된 경우
          if (cookie.getMaxAge() <= 0) {
            System.out.println("\nexpirefilter실행~!");
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            // 쿠키가 만료된 경우 로그아웃 처리
            if (auth != null) {
              System.out.printf("\n만료처리 테스트1 %s %s", cookie.getName(), cookie.getMaxAge());
              new SecurityContextLogoutHandler().logout(request, response, auth);
            }
            System.out.printf("\n만료처리 테스트2 %s %s초", cookie.getName(), cookie.getMaxAge());

            // 만료된 쿠키 삭제
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            response.sendRedirect("/customLogin?logout=true");
            System.out.printf("\n만료처리 테스트3 %s %s초", cookie.getName(), cookie.getMaxAge());
            return;
          }
        }
      }
    }

    filterChain.doFilter(request, response);
  }
}
