package com.footfix.controller;

import com.footfix.dao.FootfixDAO;
import com.footfix.domain.User;
import com.footfix.dto.FootfixUserDatesDTO;
import com.footfix.security.config.auth.PrincipalDetails;
import com.footfix.security.config.oauth.PrincipalOAuth2UserService;
import com.footfix.security.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CommonController {

  private final PrincipalOAuth2UserService principalOAuth2UserService;
  private final FootfixDAO footfixDAO;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final UserRepository userRepository;
  private final Scheduler scheduler;

  // 사용자 별명 가져오기
  private String getNickName() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
    String nickName = null;

    if(principalDetails.getUser().getProvider() != null){ // 소셜로그인
      String provider = principalDetails.getUser().getProvider();

      switch (provider){
        case "google" :
          nickName = "google_" + principalDetails.getAttributes().get("sub"); break;
        case "kakao":
          nickName = "kakao_" + principalDetails.getAttributes().get("id"); break;
        case "naver":
          nickName = "naver_" + ((Map)principalDetails.getAttributes().get("response")).get("id"); break;
      }
    }else{ // 일반로그인
      nickName = principalDetails.getUsername();
    }
    return nickName;
  }

  // 사용자 id 가져오기
  private Long getId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
    return principalDetails.getUser().getId();
  }

  @ResponseBody
  @GetMapping("/test/login")
  public  String loginTest(
          Authentication authentication,
          @AuthenticationPrincipal PrincipalDetails userDetails){
    System.out.println("/test/login ====================================================================");
    PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
    System.out.println("authentication.getPrincipal()" + principalDetails.getUser());
    System.out.println("@AuthenticationPrincipal.userDetails = " + userDetails.getUser());
    return "세션 정보 확인하기" ;
  }

  @ResponseBody
  @GetMapping("/test/oauth/login")
  public  String loginTest(
          Authentication authentication,
          @AuthenticationPrincipal OAuth2User oauth){
    System.out.println("/test/login ====================================================================");
    // Authentication extends Principal, getPrincipal()의 타입은 Object
    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
    System.out.println("authentication = " + oAuth2User.getAttributes()); // 아래와 같다
    System.out.println("OAuth2User = " + oauth.getAttributes()); // 위와 같다다
    return "OAuth 세션 정보 확인하기" ;
  }

  @PostMapping("/join")
  public String join(User user){
    System.out.println(user);
    user.setRole("ROLE_USER");
    String rawPassword = user.getPassword();
    String encodedPassword = bCryptPasswordEncoder.encode(rawPassword);
    user.setPassword(encodedPassword);

    userRepository.save(user);
    return "/customLogin";
  }

  @GetMapping("/accessError")
  public void accessDenied(Model model){
    System.out.println("접근 금지!!");
    model.addAttribute("msg","접근 금지페이지 입니다~!!");
  }

  @GetMapping("/customLogin")
  public String loginInput(String error, String logout, Model model){
    // 로그인 실패하면  /customLogin?error=true 로 리다이렉트되도록 스프링시큐리티 설정해놓았음
//    System.out.println("error : "+error);
    // 로그아웃하면 로그인페이지에 error변수에 true값 적용
//    System.out.println("logout : "+logout);

    if(error != null) {
      model.addAttribute("error","로그인 실패ㅜㅜ. 다시한번 확인해주세요");
    }
    if(logout != null) {
      model.addAttribute("logout","로그아웃~!!");
    }
    return "/customLogin";
  }//customLogin 매핑주소

  @GetMapping("/customLogout")
  public void logoutGet() {}

  @PostMapping("/customLogout") // 같은매핑주소라도 GET과 POST에 의해 구분된다. 실행되는일이 없음;;
  public void logoutPost() {}

  @RequestMapping("/welcome")
  public String naverLogin(String error){
    System.out.println("\nwelcome 컨트롤러 호출~!");
    if(error != null){
      return "/customLogin";
    }
    return "/welcome";
  }

  @RequestMapping("/index")
  public String index(
          Model model, HttpSession session, HttpServletRequest request){

    String nickName = getNickName();
    Long userId = getId();
     session = request.getSession(false); // 세션이 존재하지 않다면 생성하지않고 null반환
    String testvalue = (String) session.getAttribute("testKey");

    model.addAttribute("testvalue",testvalue);
    model.addAttribute("nickName",nickName);
    model.addAttribute("userId",userId);
    return "/index";
  }

  @GetMapping("/authRegister")
  public String authRegister() {

    return "/signup/authRegister";
  }

  // 프로필
  @GetMapping("/profile")
  public String profile(){
    return "/profile/profile";
  }

  @GetMapping("/profile-settings")
  public String profileSetting(){
    return "/profile/profile-settings";
  }

  @GetMapping("/profile-notification")
  public String profileNotification(Model model) throws SchedulerException {

    Long userId = getId();
//    List<FootfixUserDatesDTO> ffDates = this.footfixDAO.getMatchInfo(userId);
    List<FootfixUserDatesDTO> ffDates = new ArrayList<>();


    // 모든 예약된 작업 조회
    for (String groupName : scheduler.getJobGroupNames()) { // 모든 그룹이름을 List<String> 으로 불러온다
      for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) { // 각각의 그룹이름으로 등록된 jobkey를 불러온다
        JobDetail jobDetail = scheduler.getJobDetail(jobKey); // jobkey에 해당하는 job을 불러온다.

        JobKey jobKey1 = jobDetail.getKey();
        String jobId = jobKey1.getName();
        String timeString = (String) jobDetail.getJobDataMap().get("time");
        String title = (String) jobDetail.getJobDataMap().get("title");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse(timeString, formatter);
        // LocalDateTime을 Date로 변환
        Timestamp date = Timestamp.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        String[] splitTitle = title.split("vs");
        String home = splitTitle[0].trim();
        String away = splitTitle[1].trim();

        FootfixUserDatesDTO footfixUserDatesDTO = new FootfixUserDatesDTO();
        footfixUserDatesDTO.setJobId(jobId);
        footfixUserDatesDTO.setDate(date);
        footfixUserDatesDTO.setHomeName(home);
        footfixUserDatesDTO.setAwayName(away);
        ffDates.add(footfixUserDatesDTO);
        log.info("Job상세보기: " + jobDetail);
      }
    }
    log.info("Job에 저장된경기: " + ffDates);

    model.addAttribute("ffDates", ffDates);
    model.addAttribute("userId", userId);
    return "/profile/profile-notification";
  }
}
