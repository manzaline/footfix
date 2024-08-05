package com.footfix.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Data
public class FootfixUserDatesDTO {

  private String jobId;
  private Timestamp date;
  private String homeName;
  private String awayName;
  private Long userId;
}
