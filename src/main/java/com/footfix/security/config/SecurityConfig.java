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
import org.springframework.security.core.userdetails.UserDetailsService;
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
// secured 어노테이션 활성화(default = false, preAuthorize, postAuthorize 어노테이션 활성화(default = true)
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig {

  @Bean
  public AccessDeniedHandler customAccessDeniedHandler() {
    return new CustomAccessDeniedHandler();
  }

  @Bean
  public AuthenticationSuccessHandler customLoginSuccessHandler() {
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
  public final PrincipalOAuth2UserService principalOAuth2UserService;


  public SecurityConfig(
          PrincipalDetailsService principalDetailsService,
          PersistentTokenBasedRememberMeServices rememberMeServices,
          PrincipalOAuth2UserService principalOAuth2UserService) {
    this.userDetailsService = principalDetailsService;
    this.rememberMeServices = rememberMeServices;
    this.principalOAuth2UserService = principalOAuth2UserService;
  }

  @Bean
  protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
//            .csrf(csrf -> csrf.disable()) // CSRF 비활성화 .csrf(AbstractHttpConfigurer::disable)

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
            // HTTP기본인증, 헤더에 이름과 비밀번호를 인코딩하여 전송. REST API활용할때 씀.
//            .httpBasic(Customizer.withDefaults())
            .formLogin(login -> login
                    .loginPage("/customLogin")
                    .loginProcessingUrl("/loginProc") // 생략해도 기본값 /login 으로 지정되지 않음. 이유는 모름;;
                    .successHandler(customLoginSuccessHandler())
                    .failureUrl("/customLogin?error=true")
                    .permitAll()
            )
            .exceptionHandling(exception -> exception // 예외 처리
                    .accessDeniedHandler(customAccessDeniedHandler()) // 접근 거부 핸들러
            )
            .rememberMe(remember -> remember // 자동 로그인 설정
                    .key("footfix") // 기본값 : "springSecured"
                    .rememberMeParameter("remember-me")
//                    .rememberMeCookieName("footfix-remember-me")
//                    .tokenValiditySeconds(10)
                    .userDetailsService(userDetailsService)
                    .rememberMeServices(rememberMeServices) // 세션저장(MyBatisTokenRepository.java)
            )
            .logout(logout -> logout
                            // POST(대문자 필수!)방식의 요청만 받도록 설정
                            .logoutRequestMatcher(new AntPathRequestMatcher("/customLogout", "POST")) // 세부적인 커스터마이징이 가능한 로그아웃설정
//                    .logoutUrl("/customLogout") // 간단하게 로그아웃설정. csrf공격때문에 POST방식만 지원
                            .invalidateHttpSession(true) // 세션 무효화
                            .deleteCookies("footfix-remember-me") // 쿠키 삭제
                            .logoutSuccessHandler((req, resp, authentication) -> {
                              resp.sendRedirect("/customLogin?logout=true"); // 클라이언트가아닌 서버에서 주고받는
                            })
            )
            .oauth2Login(oauth2 -> oauth2
                    .loginPage("/customLogin")
                    .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                            .userService(principalOAuth2UserService)
                    )
                    .successHandler(customLoginSuccessHandler()) // formlogin과 oauth2login의 설정은 독립적이다
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
