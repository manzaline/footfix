package com.footfix.security.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class MemberDTO {

  private String userid;
  private String password;
  private String userName; // 컬럼과 변수명이 다름
  private boolean enabled;

  private Date regDate; // 컬럼과 변수명이 다름
  private Date updateDate; // 컬럼과 변수명이 다름
  private List<AuthDTO> authList;
}
