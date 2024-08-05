package com.footfix.dao;

import com.footfix.dto.FootfixUserDatesDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FootfixDAOImpl implements FootfixDAO{

  private final SqlSession sqlSession;

  public FootfixDAOImpl(SqlSession sqlSession) {
    this.sqlSession = sqlSession;
  }

  @Override
  public void saveDates(FootfixUserDatesDTO fud) {
    this.sqlSession.insert("ff_insert",fud);
  }

  @Override
  public List<FootfixUserDatesDTO> getMatchInfo(Long userId) {
    return this.sqlSession.selectList("getMatchInfo", userId);
  }

  @Override
  public List<String> getJobIdList(Long userId) {
    return this.sqlSession.selectList("getJobIdList", userId);
  }

  @Override
  public void delDates(String jobId) {
    this.sqlSession.delete("delDate", jobId);
  }

//  @Override
//  public void deleteDatesByUserId(int matchId, Long userId) {
//    Map<String, Object> params = new HashMap<>();
//    params.put("matchId", matchId);
//    params.put("userId", userId);
//    this.sqlSession.delete("delDates", params);
//  }
}
