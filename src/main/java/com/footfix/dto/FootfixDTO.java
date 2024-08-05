package com.footfix.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FootfixDTO {

  /**
   *  List<FootfixDTO>의 FootfixDTO 객체들은 response 필드가 없다.
   *  List<FootfixDTO> response 는 response 속성과 매치되어 리스트안의 객체는 @JsonIgnoreProperties때문에 response를 건너뛰고
   *  response속성의 배열부터 바인딩되기 시작하기 때문이다
   */

  private List<FootfixDTO> response;
  private Fixture fixture;
  private League league;
  private Teams teams;
  private Goals goals;

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class League {
    private String country;
    private String logo;
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Fixture {
    private Integer id; // 정수값을 int가 아닌 Integer로 받는것에 유의하자
    private String date;
    private Status status;

    public String getDate() {
      try {
        // String date의 값을 Date 객체로 변환
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        Date day = parser.parse(date);

        // Date 객체를 년월일시분 형식의 문자열로 변환
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return formatter.format(day);
      } catch (ParseException e) {
        // 예외 발생 시 기본값 반환
        return date;
      }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Status {
      @JsonProperty("long")
      private String matchStatus;
    }
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Teams {
    private Team home;
    private Team away;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Team {
      private String name;
      private String logo;

      public String getName() {
        if(name.equals("Manchester City")){
          name = "Man City";
        }else if(name.equals("Manchester United")){
          name = "Man Utd";
        }else if(name.equals("Nottingham Forest")){
          name = "Nottingham";
        }
        return name;
      }
    }
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Goals {
    private Integer home;
    private Integer away;

    public String getHome() {
      return home != null ? home.toString() : "";
    }

    public String getAway() {
      return away != null ? away.toString() : "";
    }
  }
}