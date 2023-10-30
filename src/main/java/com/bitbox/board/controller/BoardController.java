package com.bitbox.board.controller;

import com.bitbox.board.dto.request.BoardModifyRequestDto;
import com.bitbox.board.dto.request.BoardRegisterRequestDto;
import com.bitbox.board.dto.request.CategoryModifyRequestDto;
import com.bitbox.board.dto.request.CommentModifyRequestDto;
import com.bitbox.board.dto.request.CommentRegisterRequestDto;
import com.bitbox.board.dto.response.BoardDetailResponseDto;
import com.bitbox.board.dto.response.BoardPageResponseDto;
import com.bitbox.board.dto.response.BoardResponseDto;
import com.bitbox.board.dto.response.CategoryDto;
import com.bitbox.board.dto.response.CommentResponseDto;
import com.bitbox.board.service.BoardService;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

  private final BoardService boardService;

  @GetMapping("/{boardType}")
  public ResponseEntity<Page<BoardResponseDto>> getBoardList(
      @PathVariable("boardType") String boardType,
      @RequestParam("categoryId") Long categoryId,
      @PageableDefault(size = 10, sort = "created_at", direction = Sort.Direction.DESC) Pageable pageable)
      throws Exception {
    return ResponseEntity.ok(boardService.getBoardList(pageable, categoryId, boardType));
  }

  @GetMapping("/category")
  public ResponseEntity<List<CategoryDto>> getCategoryList(
      @RequestParam("categoryId") Long categoryId) {
    return ResponseEntity.ok(boardService.getCategoryList(categoryId));
  }

  @PostMapping("/{boardType}/category")
  public ResponseEntity<Boolean> registerCategory(
      @RequestParam("categoryName") String categoryName,
      @PathVariable("boardType") String boardType,
      @RequestHeader("memberAuthority") String authority) {
    return ResponseEntity.ok(boardService.registerCategory(categoryName, boardType, authority));
  }

  @PutMapping("/category")
  public ResponseEntity<Boolean> modifyCategory(
      @RequestBody CategoryModifyRequestDto categoryModifyRequestDto,
      @RequestHeader("memberAuthority") String authority) {
    return ResponseEntity.ok(boardService.modifyCategory(categoryModifyRequestDto, authority));
  }

  @DeleteMapping("/category")
  public ResponseEntity<Boolean> deleteCategory(
      @RequestParam("categoryId") Long categoryId,
      @RequestHeader("memberAuthority") String authority) {
    return ResponseEntity.ok(boardService.deleteCategory(categoryId, authority));
  }

  @GetMapping("/{boardType}/search")
  public ResponseEntity<Page<BoardResponseDto>> searchBoardList(
      @PathVariable("boardType") String boardType,
      @RequestParam("categoryId") Long categoryId,
      @RequestParam("keyword") String keyword,
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable)
      throws Exception {
    return ResponseEntity.ok(boardService.searchBoardList(pageable, categoryId, URLDecoder.decode(keyword, StandardCharsets.UTF_8), boardType));
  }

  @GetMapping("{boardType}/detail")
  public ResponseEntity<BoardDetailResponseDto> getBoardDetail(
      @RequestParam("boardId") Long boardId,
      @RequestHeader("memberId") String memberId,
      @RequestHeader("memberAuthority") String authority)
      throws Exception {

    return ResponseEntity.ok(boardService.getBoardDetail(boardId, memberId, authority));
  }

  @PostMapping("/{boardType}")
  public ResponseEntity<Boolean> registerBoard(
      @RequestBody BoardRegisterRequestDto request,
      @RequestHeader("memberId") String memberId,
      @RequestHeader("memberNickname") String memberName,
      @RequestHeader("memberProfileImg") String memberProfileImg)
      throws Exception {
    return ResponseEntity.ok(
        boardService.registerBoard(request, memberId, URLDecoder.decode(memberName, StandardCharsets.UTF_8), memberProfileImg));
  }

  @PutMapping("/{boardType}")
  public ResponseEntity<Boolean> modifyBoard(
      @RequestBody BoardModifyRequestDto request,
      @RequestHeader("memberId") String memberId,
      @RequestHeader("memberAuthority") String authority)
      throws Exception {
    return ResponseEntity.ok(boardService.modifyBoard(request, memberId, authority));
  }

  @DeleteMapping("/{boardType}")
  public ResponseEntity<Boolean> removeBoard(
      @RequestParam("boardId") Long boardId,
      @RequestHeader("memberId") String memberId,
      @RequestHeader("memberAuthority") String authority)
      throws Exception {
    return ResponseEntity.ok(boardService.removeBoard(boardId, memberId, authority));
  }

  @GetMapping("/member")
  public ResponseEntity<Page<BoardResponseDto>> getMemberBoard(
      @PageableDefault(size = 3, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
      @RequestHeader("memberId") String memberId)
      throws Exception {
    return ResponseEntity.ok(boardService.getMemberBoard(pageable, memberId));
  }

  @GetMapping("/member/comment")
  public ResponseEntity<Page<CommentResponseDto>> getMemberComment(
      @PageableDefault(size = 3, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
      @RequestHeader("memberId") String memberId)
      throws Exception {
    return ResponseEntity.ok(boardService.getMemberComment(pageable, memberId));
  }

  @PostMapping("/comment")
  public ResponseEntity<Boolean> registerComment(
      @RequestBody CommentRegisterRequestDto request,
      @RequestHeader("memberId") String memberId,
      @RequestHeader("memberNickname") String memberName,
      @RequestHeader("memberProfileImg") String memberProfileImg)
      throws Exception {
    return ResponseEntity.ok(boardService.registerComment(request, memberId, URLDecoder.decode(memberName, StandardCharsets.UTF_8), memberProfileImg));
  }

  @PutMapping("/comment")
  public ResponseEntity<Boolean> modifyComment(
      @RequestBody CommentModifyRequestDto request, @RequestHeader("memberId") String memberId)
      throws Exception {
    return ResponseEntity.ok(boardService.modifyComment(request, memberId));
  }

  @DeleteMapping("/comment/{commentId}")
  public ResponseEntity<Boolean> removeComment(
      @PathVariable("commentId") Long commentId,
      @RequestHeader("memberId") String memberId,
      @RequestHeader("memberAuthority") String authority)
      throws Exception {
    return ResponseEntity.ok(boardService.removeComment(commentId, memberId, authority));
  }
}
