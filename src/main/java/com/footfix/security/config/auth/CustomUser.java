package com.footfix.security.config.auth;

import com.footfix.security.dto.MemberDTO;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.stream.Collectors;

@Getter
public class CustomUser extends User {

  private static final long serialVersionUID = 1L;
  private MemberDTO memberDTO;

  public CustomUser(MemberDTO memberDTO) {
    super(memberDTO.getUserName(), memberDTO.getPassword(),
            memberDTO.getAuthList().stream().map(authDTO ->
                    new SimpleGrantedAuthority(authDTO.getAuth())).collect(Collectors.toList())
    );

    /**
     * public User(Stirng userid, String userpw, Collection<? extends GrantedAuthority> authorities >>> User의 생성자
     * map(Function<? super T, ? extends R>) >>> 스트림의 map()매서드는 '함수형 인터페이스 Function'만 매개변수로 받음
     * Function<T, R> 로써 단 하나의 추상메서드 'R apply(T t)'라는 T타입을 매개로받고 R타입으로 반환하는 녀석를 가짐
     *
     * DB로부터 검색된 아이디, 비번, 권한목록을 수집한 다음 생성자를 호출해서 전달한다.
     * 조상 User의 오버로딩된 생성자를 호출해서 각각의 인자값을 전달한다
     */

    System.out.println("\n!~!~CustomUser.CustomUser 호출~!~!\n");
    this.memberDTO = memberDTO;
  }

  public CustomUser(String username, String password, Collection<? extends GrantedAuthority> authorities){
    // GrantedAuthority를 상속받은 자손타입으로만 제네릭타입 형변환을 허용하면서 권한 목록을 구한다.
    super(username, password, authorities); // 조상의 오버로딩 된 생성자를 호출하면서 아디,비번,권한목록을 전달
  }
}
