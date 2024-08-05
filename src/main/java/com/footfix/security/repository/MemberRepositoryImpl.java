package com.footfix.security.repository;

import com.footfix.security.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

  private final SqlSession sqlSession;

  @Override
  public MemberDTO findByUsername(String userid) {
    return this.sqlSession.selectOne("read",userid);
  }
}
