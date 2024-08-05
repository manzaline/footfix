package com.footfix.controller;

import com.footfix.dto.BoardDTO;
import com.footfix.security.config.auth.PrincipalDetails;
import com.footfix.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

  private final BoardService boardService;

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

  @GetMapping("/adminBbs_write")
  public String adminBbs_write(Model model){
    // 작성 완료시에는 반드시 1페이지로 돌아가야한다. 따라서 page나 find파라미터를 받을필요가 없다
    return  "/admin/adminBbs_write";
  }

  @PostMapping("/adminBbs_write_ok")
  public String adminBbs_write_ok(
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
        } catch (IOException e) { e.printStackTrace(); }
      }
    }

    String realFileName = fileNames.toString().substring(1, fileNames.toString().length() - 1);
    String realFilePath = filePath.toString().substring(1, filePath.toString().length() - 1);

    boardDTO.setBbs_fileName(realFileName);
    boardDTO.setBbs_filePath(realFilePath);
    this.boardService.saveBbs(boardDTO);
    return  "redirect:adminBbs_list";
  }

  @RequestMapping("/adminBbs_list")
  public String adminBbs_list(
          Model model,
          @RequestParam(name = "selectPageSize", defaultValue = "10") String selectPageSize,
          @RequestParam(name = "page", defaultValue = "1") int currentPage,
          @ModelAttribute BoardDTO boardDTO){

    int pageSize = Integer.parseInt(selectPageSize);
    int startRow = (currentPage-1) * pageSize + 1;
    int endRow = currentPage * pageSize;
    String find_field = boardDTO.getFind_field(); String find_name = boardDTO.getFind_name();
    boardDTO.setStartRow(startRow); boardDTO.setEndRow(endRow);
    boardDTO.setFind_field(find_field);
    boardDTO.setFind_name("%"+find_name+"%");
    int rowTotalCount = boardService.getBbsTotalCount(boardDTO);

    int startPage = ((currentPage - 1) / 10) * 10 + 1;
    int maxPage = (int) Math.ceil((double) rowTotalCount / pageSize);
    int endPage = Math.min(startPage + 10 -1, maxPage); // min(a,b) a와 b중 더 작은것을 반환
    List<BoardDTO> bbsList = boardService.getBbsList(boardDTO);

    model.addAttribute("page",currentPage);
    model.addAttribute("startPage",startPage);
    model.addAttribute("endPage",endPage);
    model.addAttribute("maxPage",maxPage);
    model.addAttribute("startRow",startRow);
    model.addAttribute("endRow",endRow);
    model.addAttribute("totalCount",rowTotalCount);
    model.addAttribute("find_field",find_field);
    model.addAttribute("find_name",find_name);
    model.addAttribute("bbsList",bbsList);

//    if((find_field == null || find_field.equals("")) && currentPage == 1){
//      return "/admin/adminBbs_list";
//    }else if ((find_field == null || find_field.equals("")) && currentPage != 1){
//      return "/admin/adminBbs_list?page=" + currentPage;
//    }else if ((find_field != null || !find_field.equals("")) && currentPage == 1){
//      return "/adminBbs_list?find_field=" + find_field + "&find_name=" + find_name;
//    }else return "/adminBbs_list?page=" + currentPage + "&find_field=" + find_field + "&find_name=" + find_name;
    return "/admin/adminBbs_list";
  }

  @RequestMapping("/adminBbs_cont")
  public String adminBbs_cont(
          @RequestParam long bno, @RequestParam int page, // @RequestParam 클라이언트의 변수명과 서버변수명이 같으면 생략가능
          @RequestParam(value = "find_field", required = false) String find_field,
          @RequestParam(value = "find_name", required = false) String find_name,
          Model model){

    String nickName = getNickName();
    BoardDTO boardDTO = this.boardService.getBbsCont(bno);

    model.addAttribute("nickName",nickName);
    model.addAttribute("page",page);
    model.addAttribute("find_field", find_field);
    model.addAttribute("find_name", find_name);
    model.addAttribute("bno",bno);
    model.addAttribute("boardDTO",boardDTO);
    return "/admin/adminBbs_cont";
  }

  @RequestMapping("/adminBbs_edit")
  public String adminBbs_edit(
          @RequestParam(value = "bno") long bno, int page,
          @RequestParam(value = "find_field", required = false) String find_field,
          @RequestParam(value = "find_name", required = false) String find_name,
          Model model){

    String nickName = getNickName();
    BoardDTO boardDTO = this.boardService.getBbsContNoCount(bno);

    model.addAttribute("nickName", nickName);
    model.addAttribute("bno", bno);
    model.addAttribute("boardDTO", boardDTO);
    model.addAttribute("page", page);
    model.addAttribute("find_field", find_field);
    model.addAttribute("find_name", find_name);
    return "/admin/adminBbs_edit";
  }

  @PostMapping("/adminBbs_edit_ok")
  public String adminBbs_edit_ok(
          int page,
          @RequestParam(value = "find_field", required = false) String find_field,
          @RequestParam(value = "find_name", required = false) String find_name,
          @ModelAttribute BoardDTO boardDTO,
          @RequestParam(value = "files") MultipartFile[] multipartFiles){

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
      for (MultipartFile file : multipartFiles) {
        if (!file.isEmpty()) {
          UUID uuid = UUID.randomUUID();
          Path path = Paths.get("uploads/" + getNow() + "/"
                  + "bbs" + uuid + file.getOriginalFilename());

          try {
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            fileName.add(file.getOriginalFilename());
            filePath.add(path.toString());
          } catch (IOException e) { e.printStackTrace(); }
        }
      }

      String realFileName = fileName.toString().substring(1, fileName.toString().length() - 1);
      String realFilePath = filePath.toString().substring(1, filePath.toString().length() - 1);

      boardDTO.setBbs_fileName(realFileName);
      boardDTO.setBbs_filePath(realFilePath);
      this.boardService.editBbsCont(boardDTO);
      String encodedFindName = URLEncoder.encode(find_name,StandardCharsets.UTF_8);
      return "redirect:/admin/adminBbs_list?page=" + page + "&find_field=" + find_field + "&find_name=" + encodedFindName;
    }else {
      this.boardService.editBbsCont(boardDTO);
      String encodedFindName = URLEncoder.encode(find_name,StandardCharsets.UTF_8);
      return "redirect:/admin/adminBbs_list?page=" + page + "&find_field=" + find_field + "&find_name=" + encodedFindName;
    }
  }

  @GetMapping("/adminBbs_del")
  public String adminBbs_del(long bno, int page,
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
    model.addAttribute("page", page);
    model.addAttribute("find_field", find_field);
    model.addAttribute("find_name", find_name);
    this.boardService.delBbs(bno);

    String encodedFindName = URLEncoder.encode(find_name,StandardCharsets.UTF_8);
    return "redirect:/admin/adminBbs_list?page=" + page + "&find_field=" + find_field + "&find_name=" + encodedFindName;
  }
}
