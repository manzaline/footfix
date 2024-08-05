package com.footfix.service;

import com.footfix.dao.BoardDAO;
import com.footfix.dto.BoardDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BoardServiceImpl implements BoardService{

  private final BoardDAO boardDAO;

  public BoardServiceImpl(BoardDAO boardDAO) {
    this.boardDAO = boardDAO;
  }

  @Override
  public void saveBbs(BoardDTO boardDTO) {
    this.boardDAO.saveBbs(boardDTO);
  }

  @Override
  public void delBbs(long bno) {
    this.boardDAO.delBbs(bno);
  }

  @Override
  public List<BoardDTO> getBbsList(BoardDTO boardDTO) {
    return this.boardDAO.getBbsList(boardDTO);
  }

  @Transactional
  @Override
  public BoardDTO getBbsCont(long bno) {
    this.boardDAO.updateHit(bno);
    return this.boardDAO.getBbsCont(bno);
  }

  @Override
  public BoardDTO getBbsContNoCount(long bno) {
    return this.boardDAO.getBbsCont(bno);
  }

  @Override
  public int getBbsTotalCount(BoardDTO boardDTO) {
    return this.boardDAO.getBbsTotalCount(boardDTO);
  }

  @Override
  public void editBbsCont(BoardDTO boardDTO) {
    this.boardDAO.editBbsCont(boardDTO);
  }

  @Override
  @Transactional
  public void replyBbs(BoardDTO boardDTO) {
    this.boardDAO.depthUpReplyBbs(boardDTO);
    this.boardDAO.replyBbs(boardDTO);
  }
}
