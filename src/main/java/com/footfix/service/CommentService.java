package com.footfix.service;

import com.footfix.dto.CommentDTO;

import java.util.List;

public interface CommentService {
  void commentAdd(CommentDTO commentDTO);

  List<CommentDTO> getCommentList(long bno);

  void commentDel(long cno);

  void commentEdit(CommentDTO commentDTO);

  void commentReply(CommentDTO commentDTO);
}
