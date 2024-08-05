package com.footfix.dao;

import com.footfix.dto.MessageDTO;

public interface MessageDAO {

  void saveMessage(MessageDTO messageDTO);
}
