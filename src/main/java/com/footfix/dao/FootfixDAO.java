package com.footfix.dao;

import com.footfix.dto.FootfixUserDatesDTO;

import java.util.List;

public interface FootfixDAO {

  void saveDates(FootfixUserDatesDTO fud);
  List<FootfixUserDatesDTO> getMatchInfo(Long userId);
  List<String> getJobIdList(Long userId);
  void delDates(String jobId);
//  void deleteDatesByUserId(int matchId, Long userId);
}
