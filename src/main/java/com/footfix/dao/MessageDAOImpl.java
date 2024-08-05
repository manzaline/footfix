package com.footfix.dao;

import com.footfix.dto.MessageDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MessageDAOImpl implements MessageDAO{

  @Autowired
  private SqlSession sqlSession;

  @Override
  public void saveMessage(MessageDTO msg) {
    sqlSession.insert("in_msg", msg);
  }
}
