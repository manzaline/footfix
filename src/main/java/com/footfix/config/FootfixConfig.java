package com.footfix.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.footfix.security.repository.JpaPersistentTokenRepository;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.siot.IamportRestClient.IamportClient;
import jakarta.annotation.PostConstruct;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FootfixConfig {

//  @Value("${spring.datasource.driver-class-name}")
//  private String driverClassName;
//  @Value("${spring.datasource.url}")
//  private String url;
//  @Value("${spring.datasource.username}")
//  private String username;
//  @Value("${spring.datasource.password}")
//  private String password;

  @Value("${iamport.api_key}")
  private String apiKey;
  @Value("${iamport.api_secret}")
  private String apiSecret;

//  public final DataSource dataSource;
//  public FootfixConfig(DataSour33ce dataSource) { // dataSource에러는 intellij 버그;;
//    this.dataSource = dataSource;
//  }

  // proerties가 아닌 자바코드로 dataSouce() 설정
//  @Bean
//  public DataSource dataSource() {
//    DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
//    dataSourceBuilder.driverClassName(driverClassName);
//    dataSourceBuilder.url(url);
//    dataSourceBuilder.username(username);
//    dataSourceBuilder.password(password);
//    return dataSourceBuilder.build();
//  }

  @Bean
  public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
    SqlSessionFactoryBean session = new SqlSessionFactoryBean();
    session.setDataSource(dataSource);

    // application.yml에서 설정했음
    PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    session.setMapperLocations(resolver.getResources("classpath:mappers/*.xml"));
    // 여기부턴 있으면 설정
//    session.setTypeAliasesPackage("com.adjh.multiflex.scheduler.model");
//    session.setConfigLocation(new PathMatchingResourcePatternResolver().getResource("classpath:config/common-mybatis-config.xml"));
    return session.getObject();
  }

  @Bean
  public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory){
    return new SqlSessionTemplate(sqlSessionFactory);
  }

  // API불러오는대 사용
  @Bean
  public RestTemplate restTemplate(){
    return new RestTemplate();
  }

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() { return new BCryptPasswordEncoder(); }

  @Bean
  public ObjectMapper objectMapper() { return new ObjectMapper(); }

  @Bean
  public IamportClient iamportClient() {
    return new IamportClient(apiKey, apiSecret);
  }

  // 자동로그인 메모리인증 방식
//  @Bean
//  RememberMeServices rememberMeServices(UserDetailsService userDetailsService) {
//    TokenBasedRememberMeServices.RememberMeTokenAlgorithm encodingAlgorithm = TokenBasedRememberMeServices.RememberMeTokenAlgorithm.SHA256;
//    TokenBasedRememberMeServices rememberMe = new TokenBasedRememberMeServices("rhkr", userDetailsService, encodingAlgorithm);
//    PersistentTokenBasedRememberMeServices
//    rememberMe.setMatchingAlgorithm(TokenBasedRememberMeServices.RememberMeTokenAlgorithm.MD5);
//    return rememberMe;
//  }

  // 자동로그인 DB연동 방식
  @Bean
  public PersistentTokenBasedRememberMeServices customRememberMeServices(
          UserDetailsService userDetailsService, JpaPersistentTokenRepository tokenRepository) {

    // TokenBasedRememberMeServices 대신 PersistentTokenBasedRememberMeServices 객체를 생성한다
    PersistentTokenBasedRememberMeServices rememberMeServices =
            new PersistentTokenBasedRememberMeServices("footfix", userDetailsService, tokenRepository);
    rememberMeServices.setCookieName("footfix-remember-me");
    rememberMeServices.setTokenValiditySeconds(5000);

    // PersistentTokenBasedRememberMeServices는 setMatchingAlgorithm 메서드가 없으므로 삭제한다
//     rememberMe.setMatchingAlgorithm(TokenBasedRememberMeServices.RememberMeTokenAlgorithm.MD5);
    return rememberMeServices;
  }
}
