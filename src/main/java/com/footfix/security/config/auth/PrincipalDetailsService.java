package com.footfix.security.config.auth;

import com.footfix.domain.User;
import com.footfix.security.repository.MemberRepository;
import com.footfix.security.repository.UserRepository;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class PrincipalDetailsService implements UserDetailsService {

  /**
   *  #####################################################################################################
   *
   *  UserDetailsService 인터페이스를 구현하고, loadUserByUsername()매서드를 오버라이딩하여
   *  User객체를 반환하는 코드를 작성하면 스프링 시큐리티를 이용한 인증과 권한부여중 인증을 위한 부분이다.
   *  이 User객체를 이용해 'AuthenticationManager'인터페이스가 인증을
   *  'AccessDecisionManager'인터페이스가 권한체크를 수행한다
   *
   *  #####################################################################################################
   *
   *  PincipalDetailsService로 사용자의 정보과 인증권한을 가져오는 이유는
   *  jsp등에서 단순히 스프링 시큐리티에서 제공하는 사용자 아이디 정도가 아닌
   *  사용자의 이름,이메일같은 추가적인 정보를 이용하기 위해서이다.
   */

  @Setter(onMethod_ = {@Autowired}) //setter()메서드를 통한 자동의존성 주입
  private MemberRepository memberRepository;

  @Autowired
  private UserRepository userRepository;

  // loadUserByUsername() 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    System.out.println("\nPrincipalDetailsService.loadUserByUsername호출!!!");
    System.out.println("\n사용자 이름 : " + username);
//    MemberDTO memberDTO = this.memberRepository.findByUsername(username); // MyBatis 기법
    // JPA를 이용해 domain패키지의 User클래스로 생성된 User테이블 검색
    User userEntity = this.userRepository.findByUsername(username) // JPA 기법
            .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다"));

    return new PrincipalDetails(userEntity);

    /**
     *  오버로딩 된 생성자를 호출해서 DB로부터 검색된 회원정보를 전달
     *  UserDetails인터페이스를 구현한 User에게 상속받은 CustomUser를 만들었다.
     *  따라서 CustomUser는 UserDetails의 구현체이다.
     *  그리고 CustomUser클래스의 User생성자를 통해 만든 User객체를
     *  스프링 시큐리티에 반환하여 AUthenticationManager가 인증처리를 한다.
     */

    // 권한이 여러개일경우
    /*List<GrantedAuthority> authorities = memberDTO.getAuthList().stream()
            .map(authDTO -> new SimpleGrantedAuthority(authDTO.getAuth()))
            .collect(Collectors.toList());*/
  }
}
