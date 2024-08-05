package com.footfix.service;

import com.footfix.dto.MessageDTO;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService{

  @Override
  public void saveMessage(MessageDTO messageDTO) {
    System.out.println("MessageServiceImpl.java 이것이 호출되면 성공~!" + messageDTO.getUsername());
  }
}
