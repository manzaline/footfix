package com.footfix.service;

import com.footfix.dao.CommentDAO;
import com.footfix.dto.CommentDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService{

  private final CommentDAO commentDAO;

  public CommentServiceImpl(CommentDAO commentDAO) {
    this.commentDAO = commentDAO;
  }

  @Override
  public void commentAdd(CommentDTO commentDTO) {
    this.commentDAO.commentAdd(commentDTO);
  }

  @Override
  public List<CommentDTO> getCommentList(long bno) {
    return this.commentDAO.getCommentList(bno);
  }

  @Override
  public void commentEdit(CommentDTO commentDTO) {
    this.commentDAO.commentEdit(commentDTO);
  }

  @Override
  public void commentReply(CommentDTO commentDTO) {
    this.commentDAO.commentReply(commentDTO);
  }

  @Override
  public void commentDel(long cno) {
    this.commentDAO.commentDel(cno);
  }
}
