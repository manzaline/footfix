package com.footfix.dto;

import lombok.Data;

@Data
public class CommentDTO {

  private long bno;
  private long cno;
  private String commenter;
  private String commentText;
  private String comm_regdate; // Date타입은 json형식으로 변환을 거치면서 깨짐.
  private String comm_editdate;
  private long comm_ref;
  private int comm_step;
  private int comm_depth;
}
