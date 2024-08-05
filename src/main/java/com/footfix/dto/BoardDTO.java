package com.footfix.dto;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class BoardDTO {

  private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

  private long bbs_no;
  private String bbs_title;
  private String bbs_writer;
  private String bbs_cont;
  private Date bbs_regdate;
  private Date bbs_editdate;
  private String bbs_fileName;
  private String bbs_filePath;
  private int bbs_hit;
  private long bbs_ref;
  private int bbs_step;
  private int bbs_depth;

  private int startRow;
  private int endRow;

  private String find_field;
  private String find_name;

  public String getBbs_regdate() {
    return formatter.format(bbs_regdate);
  }

  public String getBbs_editdate() {
    return formatter.format(bbs_editdate);
  }

  @Override
  public String toString() {
    return "BoardDTO{" +
            "bbs_no=" + bbs_no +
            ", bbs_title='" + bbs_title + '\'' +
            ", bbs_writer='" + bbs_writer + '\'' +
            ", bbs_cont='" + bbs_cont + '\'' +
            ", bbs_regdate=" + bbs_regdate +
            ", bbs_editdate=" + bbs_editdate +
            ", bbs_fileName='" + bbs_fileName + '\'' +
            ", bbs_filePath='" + bbs_filePath + '\'' +
            ", bbs_hit=" + bbs_hit +
            ", bbs_ref=" + bbs_ref +
            ", bbs_step=" + bbs_step +
            ", bbs_depth=" + bbs_depth +
            ", startRow=" + startRow +
            ", endRow=" + endRow +
            ", find_field='" + find_field + '\'' +
            ", find_name='" + find_name + '\'' +
            '}';
  }
}