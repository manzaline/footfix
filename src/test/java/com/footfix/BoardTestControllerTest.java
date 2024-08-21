//package com.footfix;
//
//import com.footfix.dto.BoardDTO;
//import com.footfix.service.BoardService;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//@ExtendWith(SpringExtension.class) // Junit5는 @ExtendWith임. SpringBootTest에 포함되어있어서 생략가능, Junit4는 @RunWith(SpringRunner.class)
//@SpringBootTest
//public class BoardTestControllerTest {
//
//  @Autowired
//  private BoardService boardService;
//
//  @DisplayName("게시물 삽입")
//  @Test
//  public void testBWriteOk() {
//    for (int i = 0; i < 20; i++) {
//      BoardDTO boardDTO = new BoardDTO();
//      boardDTO.setBbs_writer("ZEROTOONE");
//      boardDTO.setBbs_title("이치카 제목" + i);
//      boardDTO.setBbs_cont("이치카 내용" + i);
//      boardService.saveBbs(boardDTO);
//      System.out.println("저장 성공~!");
//    }
//  }
//}
