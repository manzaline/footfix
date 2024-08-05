package com.footfix.security.config.oauth.provider;

import java.util.Map;

public class KakaoUserInfo implements OAuth2UserInfo{

  private Map<String, Object> attributes;

  public KakaoUserInfo(Map<String, Object> attributes) {
    this.attributes = attributes;
  }

  @Override
  public String getProviderId() {
    return attributes.get("id").toString();
  }

  @Override
  public String getProvider() {
    return "kakao";
  }

  @Override
  public String getEmail() {
//    return (String) attributes.get("profile_email");
    return "kjsggnet@test.com";
  }

  @Override
  public String getName() {
    return attributes.get("nickname").toString();
  }
}
