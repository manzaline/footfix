package com.footfix.service;

import com.footfix.dao.FootfixDAO;
import com.footfix.dto.FootfixUserDatesDTO;
import com.footfix.firebase.notification.FCMNotificationServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FootfixServiceImpl implements FootfixService{

  private final FootfixDAO footfixDAO;
  private final FCMNotificationServiceImpl fcmNotificationService;

  @Override
  @Transactional
  public void saveDates(Map<String, Object> ffdata, Long userId) {

    List<String> datas = (List<String>) ffdata.get("datas");
    List<String> myMatchList = this.footfixDAO.getJobIdList(userId);
    for (String data : datas) {
      String[] splitFootfix = data.split(",");
      String splitUserId = splitFootfix[0];
      String splitMatchId = splitFootfix[1];
      String jobId = splitUserId + "_" + splitMatchId;
      if (myMatchList.contains(jobId)){
        continue; // 이미 저장된 알림은 제외
      }

      String splitDateString = splitFootfix[2].trim();
      String splitHomeName = splitFootfix[3].trim();
      String splitAwayName = splitFootfix[4].trim();

      Timestamp matchDate = null;
      try{
        SimpleDateFormat parser = new SimpleDateFormat("yy-MM-dd HH:mm");
        Date parsedDate = parser.parse(splitDateString);
        matchDate = new Timestamp(parsedDate.getTime());
      }catch (ParseException e) { e.printStackTrace(); };

      FootfixUserDatesDTO footfixUserDatesDTO = new FootfixUserDatesDTO();
      footfixUserDatesDTO.setJobId(jobId);
      footfixUserDatesDTO.setDate(matchDate);
      footfixUserDatesDTO.setHomeName(splitHomeName);
      footfixUserDatesDTO.setAwayName(splitAwayName);
      footfixUserDatesDTO.setUserId(userId);
      this.footfixDAO.saveDates(footfixUserDatesDTO);
    };
  }

  @Override
  @Transactional
  public void delDates(List<String> jobIdList) throws SchedulerException {

    for (String jobId : jobIdList){
      fcmNotificationService.cancelNotification(jobId);
    }
  }
};
