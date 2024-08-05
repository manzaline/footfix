package com.footfix.service;

import org.quartz.SchedulerException;

import java.util.List;
import java.util.Map;

public interface FootfixService {

  void saveDates(Map<String, Object> ffdata, Long userId);
  void delDates(List<String> jobIdList) throws SchedulerException;
}
