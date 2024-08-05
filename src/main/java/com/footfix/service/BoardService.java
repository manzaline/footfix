package com.footfix.service;

import com.footfix.dto.BoardDTO;

import java.util.List;

public interface BoardService {
  void saveBbs(BoardDTO boardDTO);

  void delBbs(long bno);

  List<BoardDTO> getBbsList(BoardDTO boardDTO);

  int getBbsTotalCount(BoardDTO boardDTO);

  BoardDTO getBbsCont(long bno);

  BoardDTO getBbsContNoCount(long bno);

  void editBbsCont(BoardDTO boardDTO);

  void replyBbs(BoardDTO boardDTO);
}
