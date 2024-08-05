//package com.footfix.security.service;
//
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
//import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//public class CustomRememberMeServices extends PersistentTokenBasedRememberMeServices {
//
//  public CustomRememberMeServices(
//          @Value("${remember.me.key}") String key,
//          UserDetailsService userDetailsService,
//          @Qualifier("jpaPersistentTokenRepository") PersistentTokenRepository tokenRepository) {
//    super(key, userDetailsService, tokenRepository);
//  }
//
//  @Override
//  protected void onLoginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication successfulAuthentication) {
//    System.out.println("\n===========================onLoginSuccess 실행~!===========================");
//    super.onLoginSuccess(request, response, successfulAuthentication);
//    // 쿠키의 유효 시간을 설정
//    Cookie[] cookies = request.getCookies();
//    List<String> allCookies = new ArrayList<>();
//    for (Cookie cookie : cookies){
//      allCookies.add(cookie.getName());
//    }
//    System.out.println("\nCustomRememberMeServices 존재하는 모든쿠키: "+ allCookies);
//
//    for (Cookie cookie : cookies) {
//      if ("remember-me".equals(cookie.getName())) {
//        System.out.println("\nCustomRememberMeServices 쿠키만료시간이 적용될 쿠키이름: "+cookie.getName());
//        cookie.setMaxAge(0); // 쿠키 만료 시간 설정
//        response.addCookie(cookie);
//      }
//    }
//  }
//}
//
//
