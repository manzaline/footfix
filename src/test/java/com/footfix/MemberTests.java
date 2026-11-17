package com.footfix;

import com.footfix.domain.User;
import com.footfix.security.repository.UserRepository;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
public class MemberTests {

  @Setter(onMethod_ = @Autowired) //setter()매서드를 통한 의존성 즉 DI주입
  // @ContextConfiguration에 의해 지정된 security설정파일의 빈을 주입해준다
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Setter(onMethod_ = @Autowired)
  private DataSource dataSource; //커넥션 풀 관리 ds

  @Autowired
  UserRepository userRepository;

  @Test
  public void 회원가입() throws ParseException {
    userRepository.save(new User("admin", bCryptPasswordEncoder.encode("1234"), "admin@admin.com", "ROLE_ADMIN", null, null));
    userRepository.save(new User("manager", bCryptPasswordEncoder.encode("1234"), "manager@manager.com", "ROLE_MANAGER", null, null));
  };

//  @Test
  public void 게시물100개등록() {
    String sql="insert into bbs_footfix (bbs_title,bbs_writer,bbs_cont,bbs_filename,bbs_filepath) values(?,?,?,?,?)";

    for(int i = 1;i <= 100;i++){
      Connection con=null;
      PreparedStatement pstmt=null;

      try{
        con=dataSource.getConnection();
        pstmt=con.prepareStatement(sql);

        if(i<33) {
          pstmt.setString(1,"이치카제목" + i);
          pstmt.setString(2,"이치카" + i);
          pstmt.setString(3,"<p>이치카내용" + i + "</p>");
          pstmt.setString(4,"");
          pstmt.setString(5,"");
        }else if(i<66) {
          pstmt.setString(1,"아이자와제목" + i);
          pstmt.setString(2,"아이자와" + i);
          pstmt.setString(3,"<p>아이자와내용" + i + "</p>");
          pstmt.setString(4,"");
          pstmt.setString(5,"");
        }else{
          pstmt.setString(1,"진구지제목" + i);
          pstmt.setString(2,"진구지" + i);
          pstmt.setString(3,"<p>진구지내용" + i + "</p>");
          pstmt.setString(4,"");
          pstmt.setString(5,"");
        }

        pstmt.executeUpdate();

      }catch (Exception e) {e.printStackTrace();}
      finally {
        try{
          if(pstmt != null) pstmt.close();
          if(con != null) con.close();
        }catch (Exception e){e.printStackTrace();}
      }
    }
  }//testInsertBoard()

//  @Test
  public void testInsertMember() {

    String sql="insert into ff_member (userid,password,username) values(?,?,?)";

    for(int i=0; i<30; i++){
      Connection con=null;
      PreparedStatement pstmt=null;

      try{
        con=dataSource.getConnection(); //커넥션풀 ds로 con생성
        pstmt=con.prepareStatement(sql);
        pstmt.setString(2,bCryptPasswordEncoder.encode("pw"+i)); //비번을 암호화하여 문자열로 저장

        if(i<10){
          pstmt.setString(1,"user"+i);
          pstmt.setString(3,"일반사용자"+i);
        }else if(i<20){
          pstmt.setString(1,"manager"+i);
          pstmt.setString(3,"운영자"+i);
        }else{ //90~99
          pstmt.setString(1,"admin"+i);
          pstmt.setString(3,"관리자"+i);
        }

        pstmt.executeUpdate();

      }catch (Exception e){e.printStackTrace();}
      finally {
        try{
          if(pstmt != null) pstmt.close();
          if(con != null) con.close();
        }catch (Exception e){e.printStackTrace();}
      }
    }
  }//testInsertMember()

//  @Test
  public void testInsertAuth() {
    String sql="insert into ff_member_auth (userid,auth) values(?,?)";

    for(int i=0;i<30;i++){
      Connection con=null;
      PreparedStatement pstmt=null;

      try{
        con=dataSource.getConnection();
        pstmt=con.prepareStatement(sql);

        if(i<10) {
          pstmt.setString(1,"user"+i);
          pstmt.setString(2,"ROLE_MEMBER");
        }else if(i<20) {
          pstmt.setString(1,"manager"+i);
          pstmt.setString(2,"ROLE_MANAGER");
        }else{
          pstmt.setString(1,"admin"+i);
          pstmt.setString(2,"ROLE_ADMIN");
        }

        pstmt.executeUpdate();

      }catch (Exception e) {e.printStackTrace();}
      finally {
        try{
          if(pstmt != null) pstmt.close();
          if(con != null) con.close();
        }catch (Exception e){e.printStackTrace();}
      }
    }
  }//testInsertAuth()

  @Test
  public void mytest() throws ParseException {
    // String date의 값
    String dateStr = "2023-08-12T11:30:00+00:00";

    // String date의 값을 Date 객체로 변환
    SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
    Date date = parser.parse(dateStr);

    // Date 객체를 년월일시분 형식의 문자열로 변환
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    String formattedDate = formatter.format(date);

    // 결과 출력
    System.out.println(formattedDate); // 2023-08-12 11:30
  }
}