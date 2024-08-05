package com.footfix.security.config.auth;


import com.footfix.domain.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 *  /login주소 요청이 오면 시큐리티가 낚아채서 로그인을 진행
 *  => 로그인 성공하면 시큐리티는 SecurityContextHolder에 session을 만듦. 이 곳은 보안관련 정보를 저장하는 공간
 *  => session안에는 Authentication 타입의 객체만 저장가능. Authentication안에는 사용자의 인증정보를 가지고있음(권한, 사용자인증정보 등..)
 *  => Authentication안에는 다시 UserDetails타입 객체가 포함되어 있음. 이곳엔 구체적인정보(ID,PW,권한정보등..)
 */

@Data
public class PrincipalDetails implements UserDetails, OAuth2User {

//  private static final long serialVersionUID = 1L;
  private User user;
  private Map<String, Object> attributes;

  // 일반 로그인
  public PrincipalDetails(User user){
    System.out.println("\n일반로그인 PrincipalDetails 생성자 호출~!");
    this.user = user;
  }

  // 소셜 로그인
  public PrincipalDetails(User user, Map<String,Object> attributes){
    System.out.println("\n소셜로그인 PrincipalDetails 생성자 호출~!");
    this.attributes = attributes;
    this.user = user;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    System.out.println("\nPrincipalDetails.getAuthorities() 호출~!");
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(() -> user.getRole());

//    authorities.add(new GrantedAuthority() {
//      @Override
//      public String getAuthority() { return user.getRole(); }
//    });

    return authorities;
  }

  @Override
  public String getPassword() {
    return user.getPassword();
  }

  @Override
  public String getUsername() {
    return user.getUsername();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public String getProvider() {
    return user.getProvider();
  }

  // OAuth2User인터페이스의 매서드들...
  @Override
  public Map<String, Object> getAttributes() {
    return attributes;
  }

  @Override
  public String getName() {
    return null; // 중요하지 않고 쓰이지도 않아서 null, 사용하려면 attributes.get("sub");
  }
}
