package com.footfix.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.footfix.dao.FootfixDAO;
import com.footfix.dto.FootfixDTO;
import com.footfix.security.config.auth.PrincipalDetails;
import com.footfix.security.repository.UserRepository;
import com.footfix.service.FootfixService;
import lombok.RequiredArgsConstructor;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/footfix")
@RequiredArgsConstructor
public class FootfixController {

  private final FootfixService footfixService;

  // RestTemplate 객체 생성, 자동빈생성지원 안함
  private final RestTemplate restTemplate;

  // ObjectMapper 객체 생성, 스프링부트 자동빈생성
  private final ObjectMapper objectMapper;


  @Value("${my.api-football.url}")
  private String url;
  @Value("${my.api-football.X-RapidAPI-Key}")
  private String apiKey;
  @Value("${my.api-football.X-RapidAPI-Host}")
  private String apiHost;

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
    HttpHeaders headers = new HttpHeaders();
    headers.set("X-RapidAPI-Key", apiKey);
    headers.set("X-RapidAPI-Host", apiHost);

    // HttpEntity 생성
    HttpEntity<String> entity = new HttpEntity<String>(headers);

    // HTTP요청을 보내서 API 호출하고 응답 받기
    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

    // 응답 코드를 변수에 저장
    HttpStatusCode status = response.getStatusCode();
    try {
      if (status == HttpStatus.OK) {

        // JSON 데이터를 FootFixDTO 객체로 변환
        FootfixDTO ffd = objectMapper.readValue(response.getBody(), FootfixDTO.class);

        // FootFixDTO 객체에서 response 필드를 가져옴
        List<FootfixDTO> ffl = ffd.getResponse();

        // 모델에 리스트 추가
        model.addAttribute("ffl", ffl);
      } else {
        System.out.println("Request failed: " + response.getStatusCode());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    model.addAttribute("nickName",nickName);
    model.addAttribute("userId",userId);
    return "/footfix/boardfootfix";
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
