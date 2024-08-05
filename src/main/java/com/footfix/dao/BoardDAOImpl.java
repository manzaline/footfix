package com.footfix.dao;

import com.footfix.dto.BoardDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BoardDAOImpl implements BoardDAO{

  public final SqlSession sqlSession;

  public BoardDAOImpl(SqlSession sqlSession) {
    this.sqlSession = sqlSession;
  }

  @Override
  public void saveBbs(BoardDTO boardDTO) {
    this.sqlSession.insert("bbs_in",boardDTO);
  }

  @Override
  public void delBbs(long bno) {
    this.sqlSession.delete("bbs_del",bno);
  }

  @Override
  public BoardDTO getBbsCont(long bno) {
    return this.sqlSession.selectOne("bbs_get",bno);
  }

  @Override
  public void updateHit(long bno) {
    this.sqlSession.update("bbs_hit",bno);
  }

  @Override
  public List<BoardDTO> getBbsList(BoardDTO boardDTO) {
    return this.sqlSession.selectList("bbs_list",boardDTO);
  }

  @Override
  public int getBbsTotalCount(BoardDTO boardDTO) {
    return this.sqlSession.selectOne("bbs_count",boardDTO);
  }

  @Override
  public void editBbsCont(BoardDTO boardDTO) {
    this.sqlSession.update("bbs_edit",boardDTO);
  }

  @Override
  public void depthUpReplyBbs(BoardDTO boardDTO) {
    this.sqlSession.update("up_reply", boardDTO);
  }

  @Override
  public void replyBbs(BoardDTO boardDTO) {
    this.sqlSession.insert("in_reply", boardDTO);
  }

}
