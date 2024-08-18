package com.footfix.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.footfix.security.repository.JpaPersistentTokenRepository;
import com.siot.IamportRestClient.IamportClient;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

@Configuration
public class FootfixConfig {

  @Value("${spring.datasource.driver-class-name}")
  private String driverClassName;
  @Value("${spring.datasource.url}")
  private String url;
  @Value("${spring.datasource.username}")
  private String username;
  @Value("${spring.datasource.password}")
  private String password;
  @Value("${iamport.api_key}")
  private String apiKey;
  @Value("${iamport.api_secret}")
  private String apiSecret;

  @Bean
  public DataSource dataSource() {
    return DataSourceBuilder.create()
            .driverClassName(driverClassName)
            .url(url)
            .username(username)
            .password(password)
            .build();
  }

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

  // Remember-me DB연동 방식. 보안성이 좋고 유연하며 확장성이 뛰어남
  @Bean
  public PersistentTokenBasedRememberMeServices customRememberMeServices(
          UserDetailsService userDetailsService, JpaPersistentTokenRepository tokenRepository) {

    PersistentTokenBasedRememberMeServices rememberMeServices =
            new PersistentTokenBasedRememberMeServices("footfix", userDetailsService, tokenRepository);
    rememberMeServices.setCookieName("footfix-remember-me");
    rememberMeServices.setTokenValiditySeconds(10);
    return rememberMeServices;
  }


  // Remember-me 메모리연동 방식. DB사용 안함. 설정이 간단하고 소규모에서 사용. 보안은 좋지않음.
//  @Bean
//  RememberMeServices rememberMeServices(UserDetailsService userDetailsService) {
//    TokenBasedRememberMeServices.RememberMeTokenAlgorithm encodingAlgorithm = TokenBasedRememberMeServices.RememberMeTokenAlgorithm.SHA256;
//    TokenBasedRememberMeServices rememberMe = new TokenBasedRememberMeServices("rhkr", userDetailsService, encodingAlgorithm);
//    PersistentTokenBasedRememberMeServices
//    rememberMe.setMatchingAlgorithm(TokenBasedRememberMeServices.RememberMeTokenAlgorithm.MD5);
//    return rememberMe;
//  }
}
