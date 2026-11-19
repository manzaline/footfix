package com.footfix.controller;

import com.footfix.security.config.auth.PrincipalDetails;
import com.footfix.service.FootballDataScheduleService;
import com.footfix.service.FootfixService;
import lombok.RequiredArgsConstructor;
import org.quartz.SchedulerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/footfix")
@RequiredArgsConstructor
public class FootfixController {

  private final FootfixService footfixService;

  private final FootballDataScheduleService scheduleService;

  // 사용자 별명 가져오기
  private String getNickName() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
    String nickName = null;

    if(principalDetails.getUser().getProvider() != null){ // 소셜로그인
      String provider = principalDetails.getUser().getProvider();

      switch (provider){
        case "google":
          nickName = (String) principalDetails.getAttributes().get("name"); break;
        case "kakao":
          nickName = (String) principalDetails.getAttributes().get("name"); break;
        case "naver":
          nickName = (String) ((Map)principalDetails.getAttributes().get("response")).get("name"); break;
      }
    }else{ // 일반로그인
      nickName = principalDetails.getUsername();
    }
    return nickName;
  }

  @GetMapping("/listAll")
  public String footfix(Model model) {

    String nickName = getNickName();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
    Long userId = principalDetails.getUser().getId();
    try {
      model.addAttribute("matches", scheduleService.getSchedule().matches());
    } catch (RestClientException e) {
      Object reason = e instanceof RestClientResponseException response
          ? response.getStatusCode() : e.getMessage();
      log.warn("Could not load the Premier League schedule: {}", reason);
      model.addAttribute("matches", List.of());
      model.addAttribute("scheduleError", true);
    }

    model.addAttribute("nickName",nickName);
    model.addAttribute("userId",userId);
    return "footfix/boardfootfix";
  }

  @PostMapping("/saveDate/{userId}")
  @Transactional
  public ResponseEntity<String> saveDates(
          @RequestBody Map<String, Object> ffdata,
          @PathVariable Long userId) throws ParseException {

    this.footfixService.saveDates(ffdata, userId);
    return ResponseEntity.ok().body("DB에 저장하였습니다!");
  }

  @PostMapping("/delDate/{userId}")
  @Transactional
  public ResponseEntity<String> delDates(
          @PathVariable Long userId,
          @RequestBody List<String> jobIdList) throws SchedulerException {

    this.footfixService.delDates(jobIdList);
    return ResponseEntity.ok().body("알림을 삭제하였습니다~!");
  }

}
