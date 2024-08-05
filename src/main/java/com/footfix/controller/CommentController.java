package com.footfix.controller;

import com.footfix.dto.CommentDTO;
import com.footfix.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comment")
public class CommentController {

  private final CommentService commentService;

  public CommentController(CommentService commentService) {
    this.commentService = commentService;
  }

  /**
   *  <매핑속성 produces의 종류>
   *  1. application/json: JSON 형식의 데이터를 나타냅니다. 반환 타입은 주로 ResponseEntity<T>
   *  2. application/xml: XML 형식의 데이터를 나타냅니다. 반환 타입은 주로 ResponseEntity<T>
   *  3. text/plain: 일반 텍스트를 나타냅니다. 반환 타입은 주로 ResponseEntity<String>
   *  4. text/html: HTML 형식의 데이터를 나타냅니다. 반환 타입은 주로 ResponseEntity<String>
   *  5. image/jpeg: JPEG 이미지를 나타냅니다. 반환 타입은 주로 ResponseEntity<byte[]>
   *  6. application/pdf: PDF 문서를 나타냅니다. 반환 타입은 주로 ResponseEntity<byte[]>
   *
   *  따라서 반환타입이 적절하다면 produces는 생략 가능
   */

  // 댓글등록
  @PostMapping(value = "", produces = "application/json")
  public ResponseEntity<String> commentAdd(@RequestBody CommentDTO commentDTO){
    this.commentService.commentAdd(commentDTO);
    return ResponseEntity.ok().body("서버! 댓글등록에 성공하였습니다~!");
  }

  // 댓글목록 불러오기
  @GetMapping("/{bbs_no}")
  public ResponseEntity<List<CommentDTO>> commentList(@PathVariable(value = "bbs_no") long bno){
    List<CommentDTO> commentDTOList = this.commentService.getCommentList(bno);
    return ResponseEntity.ok().body(commentDTOList);
  }

  // 댓글삭제
  @DeleteMapping // url없으면 완전생략가능. 이게되네..,;;
  public ResponseEntity<String> commentDelete(@RequestBody Map<String, Long> data){ // @PathVariable로하면 간단, 연습삼아 @RequestBody Map으로햅좀
    long cno = data.get("cno");
    this.commentService.commentDel(cno);
    return ResponseEntity.ok().body("댓글 삭제에 성공했다~!");
  }

  // 댓글수정
//  @PutMapping("/{cno}")
//  @PatchMapping("/{cno}")
  @PutMapping()
  public ResponseEntity<String> commentEdit(@RequestBody CommentDTO commentDTO){
    this.commentService.commentEdit(commentDTO);
    return ResponseEntity.ok().body("서버요청! 댓글수정 성공!");
  }

  // 대댓글
  @PostMapping("/reply")
  public ResponseEntity<String> commentReply(@RequestBody CommentDTO commentDTO){
    this.commentService.commentReply(commentDTO);
    return ResponseEntity.ok().body("성공");
  }
}
