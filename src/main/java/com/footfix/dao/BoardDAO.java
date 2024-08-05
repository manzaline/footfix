package com.footfix.dao;

import com.footfix.dto.BoardDTO;

import java.util.List;

public interface BoardDAO {
  void saveBbs(BoardDTO boardDTO);

  void delBbs(long bno);

  List<BoardDTO> getBbsList(BoardDTO boardDTO);

  int getBbsTotalCount(BoardDTO boardDTO);

  BoardDTO getBbsCont(long bno);

  void updateHit(long bno);

  void editBbsCont(BoardDTO boardDTO);

  void replyBbs(BoardDTO boardDTO);

  void depthUpReplyBbs(BoardDTO boardDTO);
}
