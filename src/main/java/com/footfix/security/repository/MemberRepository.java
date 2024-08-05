package com.footfix.security.repository;

import com.footfix.security.dto.MemberDTO;

public interface MemberRepository {

  MemberDTO findByUsername(String username);
}
