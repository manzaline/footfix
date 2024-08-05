package com.footfix.security.config.oauth;

import com.footfix.domain.User;
import com.footfix.security.config.auth.PrincipalDetails;
import com.footfix.security.config.oauth.provider.GoogleUserInfo;
import com.footfix.security.config.oauth.provider.KakaoUserInfo;
import com.footfix.security.config.oauth.provider.NaverUserInfo;
import com.footfix.security.config.oauth.provider.OAuth2UserInfo;
import com.footfix.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class PrincipalOAuth2UserService extends DefaultOAuth2UserService {

  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Autowired
  private UserRepository userRepository;

  /**
   *  << 소셜로그인 전체적인 흐름 >>
   *  소셜로그인클릭 => 로그인창 => 로그인완료 => code값 반환(OAuth-Client라이브러리가 받음) => AccessToken요청
   *   => userRequest 정보 => loadUser 함수 호출 => 소셜로부터 회원프로필 받음
   */

  // loadUser는 소셜로그인으로 부터 받은 데이터를 후처리하는 함수
  // loadUserByUsername() 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    // registration으로 어떤 OAuth로 로그인했는지 확인
    System.out.println("\n소셜로그인이기때문에 PrincipalOAuth2UserService.loadUser 호출~!\n");
    System.out.println("엑세스토큰(userRequest.getAccessToken().getTokenValue()): " + userRequest.getAccessToken().getTokenValue());

    // ClientRegistration{registrationId='google', clientId='1051510044080-7ep9okc516p8kij1s7bv56jg5u47ko93.apps.googleusercontent.com', clientSecret='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', clientAuthenticationMethod=client_secret_basic, authorizationGrantType=org.springframework.security.oauth2.core.AuthorizationGrantType@5da5e9f3, redirectUri='{baseUrl}/{action}/oauth2/code/{registrationId}', scopes=[email, profile], providerDetails=org.springframework.security.oauth2.client.registration.ClientRegistration$ProviderDetails@767c7415, clientName='Google'}
    System.out.println("인증정보(userRequest.getClientRegistration()): " + userRequest.getClientRegistration()); // 인증정보

    OAuth2User oAuth2User = super.loadUser(userRequest);

    // {sub=113613430467100011476, name=ZEROTOONE, given_name=ZEROTOONE, picture=https://lh3.googleusercontent.com/a/ACg8ocKH6YMv0EGTi8opQVsHPMZzAoB_wOKSl9PiP7BgmksG=s96-c, email=kjsggnet@gmail.com, email_verified=true, locale=ko}
    System.out.println("신상정보(super.loadUser(userRequest)): " + oAuth2User.getAttributes()); // 신상정보
    System.out.println("브랜드명을 찾아보자: " + userRequest.getClientRegistration().getRegistrationId());

    // 회원가입을 강제로 진행해볼 예정
    OAuth2UserInfo oAuth2UserInfo = null;
    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    Map<String, Object> attributes = oAuth2User.getAttributes();
    if(registrationId.equals("google")){
      oAuth2UserInfo = new GoogleUserInfo(attributes);
    }else if (registrationId.equals("naver")){
      oAuth2UserInfo = new NaverUserInfo((Map)attributes.get("response"));
    }else if (registrationId.equals("kakao")){
      oAuth2UserInfo = new KakaoUserInfo(attributes);
    }else{
      System.out.println("우리는 구글,네이버,카카오만 지원합니당~!");
    }

    String provider = oAuth2UserInfo.getProvider(); // google
    String providerId = oAuth2UserInfo.getProviderId(); // 20495739857948
    String username = provider+"_"+providerId; // google_20495739857948
    String password = bCryptPasswordEncoder.encode("footfix");
    String email = oAuth2UserInfo.getEmail();
    String role = "ROLE_MEMBER";

    User userEntity = userRepository.findByUsername(username)
//            .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다!"));
            .orElse(null);
    if (userEntity == null) {
      System.out.println("없는 회원이네요. 회원가입을 시작합니다~!!");
      userEntity = User.builder()
              .username(username)
              .password(password)
              .email(email)
              .role(role)
              .provider(provider)
              .providerId(providerId)
              .build();
      userRepository.save(userEntity);
    }else {
      System.out.println("이미 가입된 회원입니다.");
    }

    return new PrincipalDetails(userEntity,oAuth2User.getAttributes());
  }
}
