package com.footfix.security.config;

import com.footfix.security.CustomAccessDeniedHandler;
import com.footfix.security.CustomLoginSuccessHandler;
import com.footfix.security.config.auth.PrincipalDetailsService;
import com.footfix.security.config.oauth.PrincipalOAuth2UserService;
import jakarta.servlet.http.HttpSessionEvent;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.time.LocalDateTime;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록
// secured 어노테이션 활성화(default = false), preAuthorize, postAuthorize 어노테이션 활성화(default = true)
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig {

  @Bean
  public CustomAccessDeniedHandler customAccessDeniedHandler() {
    return new CustomAccessDeniedHandler();
  }

  @Bean
  public CustomLoginSuccessHandler customLoginSuccessHandler() {
    return new CustomLoginSuccessHandler();
  }

  @Bean
  public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
    return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher() {
      @Override
      public void sessionCreated(HttpSessionEvent event) {
        super.sessionCreated(event);
        System.out.printf("\n===>> [%s] 세션 생성됨 %s", LocalDateTime.now(), event.getSession().getId());
      }

      @Override
      public void sessionDestroyed(HttpSessionEvent event) {
        super.sessionDestroyed(event);
        System.out.printf("\n===>> [%s] 세션 만료됨 %s", LocalDateTime.now(), event.getSession().getId());
      }

      @Override
      public void sessionIdChanged(HttpSessionEvent event, String oldSessionId) {
        super.sessionIdChanged(event, oldSessionId);
        System.out.printf("\n===>> [%s] 세션 아이디 변경  %s:%s", LocalDateTime.now(), oldSessionId, event.getSession().getId());
      }
    });
  }

  public final UserDetailsService userDetailsService;
  public final RememberMeServices rememberMeServices;
  public final DefaultOAuth2UserService principalOAuth2UserService;

  public SecurityConfig(
          PrincipalDetailsService principalDetailsService,
          PersistentTokenBasedRememberMeServices rememberMeServices,
          PrincipalOAuth2UserService principalOAuth2UserService) {
    this.userDetailsService = principalDetailsService;
    this.rememberMeServices = rememberMeServices;
    this.principalOAuth2UserService = principalOAuth2UserService;
  }

  @Bean
  protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
//            .csrf(csrf -> csrf.disable()) // CSRF 비활성화 .csrf(AbstractHttpConfigurer::disable)
//            .httpBasic(Customizer.withDefaults()) // HTTP기본인증, 헤더에 이름과 비밀번호를 인코딩하여 전송. REST API활용할때 씀.
            .authorizeHttpRequests(auth -> auth // 인가 설정
                            .requestMatchers("/img/**","/vendor/**","/fonts/**","/data/**","/assets/**","/images/**","/css/**","/js/**").permitAll()
                            .requestMatchers("/test/**","/home","/message","/mytest","/join", "/sample/all", "/welcome").permitAll()
                            //  !!!스프링시큐리티가 자동으로 접두사에 "ROLE_"을 추가해준다!!!
                            .requestMatchers("/sample/member","/index").hasAnyRole("MEMBER","MANAGER","ADMIN")
                            .requestMatchers("/sample/manager","/bbs/**","/comment").hasAnyRole("MANAGER","ADMIN","MEMBER")
                            .requestMatchers("/sample/admin","/footfix/**").hasRole("ADMIN")
                            .anyRequest().authenticated() // 나머지 요청은 인증 필요
//                      .anyRequest().permitAll()
            )
            .formLogin(login -> login
                    .loginPage("/customLogin")
                    .loginProcessingUrl("/loginProc") // 생략해도 기본값 /login 으로 지정되지 않음. 이유는 모름;;
                    .successHandler(customLoginSuccessHandler())
                    .failureUrl("/customLogin?error=true")
                    .permitAll()
            )
            .oauth2Login(oauth2 -> oauth2
                    .loginPage("/customLogin")
                    .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                            .userService(principalOAuth2UserService)
                    )
                    .successHandler(customLoginSuccessHandler()) // formlogin과 oauth2login의 설정은 독립적이다
            )
            /**
             * ====================================  < Remember-me >  ==========================================================================
             * 1. remember-me 쿠키의 토큰값에 담긴 정보
             *    username: 사용자 로그인 ID
             *    token: 임의의 토큰 값 (일반적으로 UUID)
             *    tokenSignature: 토큰의 무결성을 확인하기 위한 서명 (일반적으로 HMAC-SHA256)
             * 2. remember-me는 JSESSIONID(세션의 쿠키)가 만료되어도 로그인을 유지시킨다
             * 3. 소셜로그인은 remember-me를 구현할 수 없다. 따라서 소셜로그인은 세션타임아웃이 로그인 유효시간을 결정한다.
             * ==================================================================================================================================
             */
            .rememberMe(remember -> remember // 자동 로그인 설정
//                    .key("footfix") // 기본값 : "springSecured"
                            .rememberMeParameter("remember-me")
//                    .rememberMeCookieName("footfix-remember-me")
//                    .tokenValiditySeconds(10)
                            .userDetailsService(userDetailsService)
                            .rememberMeServices(rememberMeServices) // 세션저장(MyBatisTokenRepository.java)
            )
            /**
             * ====================================  < Session >  ==========================================================================
             * 1. 로그인에 성공하면 WAS는 아래의 예외를 제외하곤 반드시 세션을 생성한다
             * 2. 예외1: SessionCreationPolicy가 IF_REQUIRED 일때(세션이 필요한경우에만생성. 예를들어 인증이 필요할때)
             *          SessionCreationPolicy가 NEVER 일때(절대로 생성하지 않음. 개발자가 직접 생성한 경우만 이를 사용)
             *          SessionCreationPolicy가 STATELESS 일때(세션 사용 안함)
             *    예외2: stateless 인증처리 방식인경우 (소셜로그인과 같은 REST API방식의 로그인, 모바일앱 등...)
             * 3. Tomcat(Spring은 기본 서블릿 컨테이너를 Tomcat으로 사용한다)는 세션을 생성하면 세션의 ID를 쿠키에 담아 클라이언트에
             *    전송하는대 이것이 JSESSIONID(쿠키) 이며 이 쿠키의 값(Cookie Value, 쿠키의 토큰값)에는 세션ID가 들어있다(직렬화?).
             * 4. 일반적으로 사용자의 정보(쇼핑카트목록, 최근에본 물건 등...)는 이 세션에 담는다
             * 5. 그 외에 세션을 개발자가 임의로 만들면 그것은 스프링이만든 세션과는 완전히 독립적인 세션이며 전혀 관련이 없다
             *    따라서 임의로만든 세션이 만료되더라도 사용자 정의 데이터만 날아갈뿐 로그아웃은 되지 않는다. 로그아웃은 오로지 JSESSIONID만 담당한다
             * 6. HttpSession session = request.getSession() 은 이미 세션이 존재한다면 그 세션을 가져오고
             *    만약 존재하지 않는다면 세션을 새로 생성한다. 이때 getSession(false)로 하면 세션을 가져오기만 하고 생성하지는 않고 null을 반환한다
             * 7.
             * ===============================================================================================================================
             */
            // 세션 생성
            .sessionManagement(httpSecuritySessionManagementConfigurer -> {
              httpSecuritySessionManagementConfigurer
                      .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED); // 생략해도 기본값: IF_REQUIRED
            })
            .exceptionHandling(exception -> exception // 예외 처리
                    .accessDeniedHandler(customAccessDeniedHandler()) // 접근 거부 핸들러
            )
            .logout(logout -> logout
                            // POST(대문자 필수!)방식의 요청만 받도록 설정
                            .logoutRequestMatcher(new AntPathRequestMatcher("/customLogout", "POST")) // 세부적인 커스터마이징이 가능한 로그아웃설정
//                            .logoutUrl("/customLogout") // 간단하게 로그아웃설정. csrf공격때문에 POST방식만 지원
//                            .invalidateHttpSession(true) // 세션 무효화 기본값 true
                            .deleteCookies("footfix-remember-me") // 쿠키 삭제
                            .logoutSuccessHandler((req, resp, authentication) -> {
                              resp.sendRedirect("/customLogin?logout=true"); // 클라이언트가아닌 서버에서 주고받는
                            })
            )
            .build();
  }
}

  /*@Autowired
  public void configurationGlobal(AuthenticationManagerBuilder auth) throws Exception{
    auth.inMemoryAuthentication()
            .withUser("member")
            .password(passwordEncoder().encode("member"))
            .roles("MEMBER")
            .and()
            .withUser("manager")
            .password(passwordEncoder().encode("manager"))
            .roles("MEMBER","MANAGER")
            .and()
            .withUser("admin")
            .password(passwordEncoder().encode("admin"))
            .roles("MEMBER","MANAGER","ADMIN");
  }*/


  /*@Bean
  public UserDetailsService userDetailsService() {
    UserDetails member =
            User.builder()
                    .username("member")
                    .password(passwordEncoder().encode("member"))
                    .roles("MEMBER") // !!!스프링 시큐리티가 자동으로 접두사에 "ROLE_"를 붙여준다!!!
                    .build();

    UserDetails manager =
            User.builder()
                    .username("manager")
                    .password(passwordEncoder().encode("manager"))
                    .roles("MANAGER","MEMBER")
                    .build();

    UserDetails admin =
            User.builder()
                    .username("admin")
                    .password(passwordEncoder().encode("admin"))
                    .roles("ADMIN","MEMBER","MANAGER")
                    .build();

    return new InMemoryUserDetailsManager(admin, manager, member);
  }
}*/


/*
  // 스프링 시큐리티6부터 생긴 비밀번호 강제 인코딩설정을 해제
  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService()).passwordEncoder(NoOpPasswordEncoder.getInstance());
  }
*/
