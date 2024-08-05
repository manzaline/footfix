package com.footfix.security.repository;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
// 스프링 시큐리티에서 PersistentTokenBasedRememberMeServices를 사용하려면
// 반드시 PersistentTokenRepository의 구현체를 만들어주어야한다. 스프링시큐리티에서 자동 구현 X
public class MyBatisTokenRepositoryImpl implements PersistentTokenRepository {

  private final SqlSession sqlSession;

  @Override
  public void createNewToken(PersistentRememberMeToken token) {
    System.out.println("\n마이바티스 remember-me 토큰저장~!");
    sqlSession.insert("insertToken", token);
  }

  @Override
  public void updateToken(String series, String tokenValue, Date lastUsed) {
    System.out.println("\n마이바티스 remember-me 토큰 업데이트~!!");

    Map<String, Object> params = new HashMap<>();
    params.put("series", series);
    params.put("tokenValue", tokenValue);
    params.put("lastUsed", lastUsed);
    sqlSession.update("updateToken", params);
  }

  @Override
  public PersistentRememberMeToken getTokenForSeries(String seriesId) {
    System.out.println("\n마이바티스 remember-me 토큰가져오기~!");
    return sqlSession.selectOne("getTokenForSeries", seriesId);
  }

  @Override
  public void removeUserTokens(String username) {
    System.out.println("\n마이바티스 remember-me 토큰 삭제~!");
    sqlSession.delete("removeUserTokens", username);
  }
}

