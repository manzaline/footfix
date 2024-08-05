package com.footfix.controller;

import com.footfix.dto.BoardDTO;
import com.footfix.security.config.auth.PrincipalDetails;
import com.footfix.service.BoardService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/bbs")
public class BoardController {

  private final BoardService boardService;

  public BoardController(BoardService boardService) {
    this.boardService = boardService;
  }

  // 사용자 별명 가져오기
  private String getNickName() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
    String nickName;

    if(principalDetails.getUser().getProvider() != null){ // 소셜로그인
      switch (principalDetails.getUser().getProvider()){
        case "google":
          nickName = (String) principalDetails.getAttributes().get("name"); break;
        case "kakao":
          nickName = (String) principalDetails.getAttributes().get("name"); break;
        case "naver":
          nickName = (String) ((Map)principalDetails.getAttributes().get("response")).get("name"); break;
        default: throw new IllegalArgumentException("provider이 없어용: " + principalDetails.getUser().getProvider());
      }
    }else{ // 일반로그인
      nickName = principalDetails.getUsername();
    }
    return nickName;
  }

  // 폴더명을 만들때 사용합니다
  public String getNow() {
    LocalDate localDate = LocalDate.now();
    int date = localDate.getDayOfMonth();
    int month = localDate.getMonthValue();
    int year = localDate.getYear();
    return year+"-"+month+"-"+date;
  }

  @GetMapping("/b_write")
  public String b_write(Model model){
    // 작성 완료시에는 반드시 1페이지로 돌아가야한다. 따라서 page나 find파라미터를 받을필요가 없다
    String nickName = getNickName();
    model.addAttribute("nickName",nickName);
    return  "/bbs/bbs_write";
  }

  @PostMapping("/b_write_ok")
  public String b_write_ok(
          @ModelAttribute BoardDTO boardDTO,
          @RequestParam(value = "files",required = false) MultipartFile[] files){

    List<String> fileNames = new ArrayList<>();
    List<String> filePath = new ArrayList<>();
    for (MultipartFile file : files) { // MultipartFile[] files는 첨부파일이 없어도 null이 아니고 length가 0이아니라 1이라서 검증이 불가능하다
      if(!file.isEmpty()){

        UUID uuid = UUID.randomUUID();
        Path path = Paths.get("uploads/" + getNow() + "/"
                + "bbs" + uuid + file.getOriginalFilename());

        try {
          Files.createDirectories(path.getParent());
          Files.write(path, file.getBytes());

          fileNames.add(file.getOriginalFilename());
          filePath.add(path.toString());
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    String realFileName = fileNames.toString().substring(1, fileNames.toString().length() - 1);
    String realFilePath = filePath.toString().substring(1, filePath.toString().length() - 1);

    boardDTO.setBbs_fileName(realFileName);
    boardDTO.setBbs_filePath(realFilePath);
    this.boardService.saveBbs(boardDTO);
    return  "redirect:b_list";
  }

  @RequestMapping("/b_list")
  public String bbs_list(
          Model model,
          @RequestParam(value = "pageSize", defaultValue = "10") String selectedPageSize,
          @RequestParam(value = "page", defaultValue = "1") int currentPage,
          @ModelAttribute BoardDTO boardDTO){

    int pageSize = Integer.parseInt(selectedPageSize);
    int startRow = ((currentPage - 1) * pageSize) + 1;
    int endRow = startRow + pageSize-1;
    boardDTO.setStartRow(startRow);
    boardDTO.setEndRow(endRow);

    String find_field = boardDTO.getFind_field();
    String find_name = boardDTO.getFind_name();
    boardDTO.setFind_field(find_field);
    boardDTO.setFind_name("%" + find_name + "%");

    int bbsTotalCount = this.boardService.getBbsTotalCount(boardDTO);

    int startPage = (( currentPage - 1 ) / 10 ) * 10 + 1;
    int maxPage = (int) Math.ceil( (double) bbsTotalCount / pageSize );
    int endPage = maxPage;
    if(startPage < maxPage-10+1) { endPage = startPage + 10 - 1; }

    List<BoardDTO> bbsList = this.boardService.getBbsList(boardDTO);

    model.addAttribute("page",currentPage);
    model.addAttribute("boardDTO",boardDTO);
    model.addAttribute("bbsList",bbsList);
    model.addAttribute("startPage",startPage);
    model.addAttribute("endPage",endPage);
    model.addAttribute("maxPage",maxPage);
    model.addAttribute("bbsTotalCount",bbsTotalCount);
    model.addAttribute("find_field", find_field);
    model.addAttribute("find_name", find_name);
    return "/bbs/bbs_list";
  }

  @RequestMapping("/b_cont")
  public String bbs_cont(
          @RequestParam("bno") long bno, int page,
          @RequestParam(value = "find_field", required = false) String find_field,
          @RequestParam(value = "find_name", required = false) String find_name,
          Model model){

    String nickName = getNickName();
    BoardDTO boardDTO = this.boardService.getBbsCont(bno);

    model.addAttribute("nickName",nickName);
    model.addAttribute("page",page);
    model.addAttribute("boardDTO",boardDTO);
    model.addAttribute("find_field", find_field);
    model.addAttribute("find_name", find_name);
    model.addAttribute("bno",bno);
    model.addAttribute("boardDTO",boardDTO);
    return "/bbs/bbs_cont";
  }

  @GetMapping("/b_edit")
  public String bbs_edit(
          @RequestParam(value = "bno") long bno, int page,
          @RequestParam(value = "find_field", required = false) String find_field,
          @RequestParam(value = "find_name", required = false) String find_name,
          Model model){

    String nickName = getNickName();
    BoardDTO boardDTO = this.boardService.getBbsContNoCount(bno);

    model.addAttribute("nickName",nickName);
    model.addAttribute("bno",bno);
    model.addAttribute("boardDTO",boardDTO);
    model.addAttribute("page",page);
    model.addAttribute("find_field", find_field);
    model.addAttribute("find_name", find_name);
    return "/bbs/bbs_edit";
  }

  @PostMapping("/b_edit_ok")
  public String bbs_edit_ok(
          int page,
          @RequestParam(value = "find_field", required = false) String find_field,
          @RequestParam(value = "find_name", required = false) String find_name,
          @ModelAttribute BoardDTO boardDTO,
          @RequestParam(value = "files") MultipartFile[] files){

    String getOldFilePath = this.boardService.getBbsContNoCount(boardDTO.getBbs_no()).getBbs_filePath();
    if(getOldFilePath != null && !getOldFilePath.equals("")) {
      String[] getOldFilePaths = getOldFilePath.split(",");
      for (String oldFilePath : getOldFilePaths) {
        Path path = Paths.get(oldFilePath.trim()); // List가 저장할때 1번첨부, 2번첨부 와 같이 ,다음에 띄어쓰기가들어가서 trim() 적용
        try {
          Files.delete(path);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      List<String> fileName = new ArrayList<>();
      List<String> filePath = new ArrayList<>();
      // null검증이 먼저 이루어져야 NullPinterException이 안터진다
      for (MultipartFile file : files) {
        if (!file.isEmpty()) {
          UUID uuid = UUID.randomUUID();
          Path path = Paths.get("uploads/" + getNow() + "/"
                  + "bbs" + uuid + file.getOriginalFilename());

          try {
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            fileName.add(file.getOriginalFilename());
            filePath.add(path.toString());
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }

      String realFileName = fileName.toString().substring(1, fileName.toString().length() - 1);
      String realFilePath = filePath.toString().substring(1, filePath.toString().length() - 1);

      boardDTO.setBbs_fileName(realFileName);
      boardDTO.setBbs_filePath(realFilePath);
      this.boardService.editBbsCont(boardDTO);
      String encodedFindName = URLEncoder.encode(find_name,StandardCharsets.UTF_8);
      return "redirect:/bbs/b_list?page=" + page + "&find_field="+find_field+"&find_name="+encodedFindName;
    }else {
      this.boardService.editBbsCont(boardDTO);
      String encodedFindName = URLEncoder.encode(find_name,StandardCharsets.UTF_8);
      return "redirect:/bbs/b_list?page=" + page + "&find_field="+find_field+"&find_name="+encodedFindName;
    }
  }

  @GetMapping("/b_reply")
  public String b_reply(Model model, long bno, int page,
                        @ModelAttribute BoardDTO boardDTO) {

    String nickName = getNickName();

    BoardDTO dbBoardDTO = boardService.getBbsContNoCount(bno);
    dbBoardDTO.setBbs_title("Re: " + dbBoardDTO.getBbs_title());
    dbBoardDTO.setBbs_cont("<p>이전글: </p>" + dbBoardDTO.getBbs_cont().replace("<p>", "<p>>> "));
    dbBoardDTO.setFind_field(boardDTO.getFind_field());
    dbBoardDTO.setFind_name(boardDTO.getFind_name());

    model.addAttribute("page", page);
    model.addAttribute("nickName", nickName);
    model.addAttribute("boardDTO", dbBoardDTO);
    return "/bbs/bbs_reply";
  }

  @PostMapping("/b_reply_ok")
  public String b_reply_ok(Model model, int page, String find_field, String find_name,
                           BoardDTO boardDTO, MultipartFile[] files) {

    List<String> fileNames = new ArrayList<>();
    List<String> filePaths = new ArrayList<>();
    for (MultipartFile file : files){
      if(!file.isEmpty()){
        UUID uuid = UUID.randomUUID();
        Path path = Path.of("uploads/"+ getNow() + "/" + uuid + file.getOriginalFilename());

        try {
          Files.createDirectories(path.getParent());
          Files.write(path, file.getBytes());

          fileNames.add(file.getOriginalFilename());
          filePaths.add(path.toString());
        }catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    String realFileNames = fileNames.toString().substring(1, fileNames.toString().length()-1);
    String realFilePaths = filePaths.toString().substring(1, filePaths.toString().length()-1);
    boardDTO.setBbs_fileName(realFileNames);
    boardDTO.setBbs_filePath(realFilePaths);
    boardService.replyBbs(boardDTO);
    String encodedFindName = URLEncoder.encode(find_name,StandardCharsets.UTF_8);
    return "redirect:/bbs/b_list?page=" + page + "&find_field="+find_field+"&find_name="+encodedFindName;
  }

  @GetMapping("/b_delete")
  public String b_delete(long bno, int page,
                         @RequestParam(value = "find_field", required = false) String find_field,
                         @RequestParam(value = "find_name", required = false) String find_name,
                         Model model) {

    String getOldFilePath = this.boardService.getBbsContNoCount(bno).getBbs_filePath();
    if (getOldFilePath != null && !getOldFilePath.equals("")){
      String[] getOldFilePaths = getOldFilePath.split(",");
      for (String oldFilePath : getOldFilePaths){
        Path path = Paths.get(oldFilePath.trim());
        try{
          Files.delete(path);
        }catch (IOException e){
          e.printStackTrace();
        }
      }
    }
    model.addAttribute("find_field", find_field);
    model.addAttribute("find_name", find_name);
    this.boardService.delBbs(bno);
    String encodedFind_name = URLEncoder.encode(find_name,StandardCharsets.UTF_8);
    return "redirect:/bbs/b_list?page=" + page + "&find_field="+find_field+"&find_name="+encodedFind_name;
  }
}
