package com.footfix.firebase.notification.scheduler;

import lombok.Data;

import java.util.List;

@Data
public class NotificationRequestDto {
  private List<String> tokenList;
  private List<String> datas;
  private String image;
}
