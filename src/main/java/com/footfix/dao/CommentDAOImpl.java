package com.footfix.dao;

import com.footfix.dto.CommentDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommentDAOImpl implements CommentDAO{

  private final SqlSession sqlSession;

  public CommentDAOImpl(SqlSession sqlSession) {
    this.sqlSession = sqlSession;
  }

  @Override
  public void commentAdd(CommentDTO commentDTO) {
    this.sqlSession.insert("comm_in",commentDTO);
  }

  @Override
  public List<CommentDTO> getCommentList(long bno) {
    return this.sqlSession.selectList("comm_list",bno);
  }

  @Override
  public void commentEdit(CommentDTO commentDTO) {
    this.sqlSession.update("comm_edit",commentDTO);
  }

  @Override
  public void commentReply(CommentDTO commentDTO) {
    this.sqlSession.insert("reply_in", commentDTO);
  }

  @Override
  public void commentDel(long cno) {
    this.sqlSession.delete("comm_del",cno);
  }
}
