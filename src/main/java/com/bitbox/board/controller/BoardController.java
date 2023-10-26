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
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
  public ResponseEntity<List<BoardPageResponseDto>> getBoardList(
      @PathVariable("boardType") String boardType,
      @RequestParam("category") Long categoryId,
      @PageableDefault(size = 4, sort = "created_at,desc") Pageable pageable)
      throws Exception {

    List<CategoryDto> categoryList = boardService.getCategoryList(categoryId);
    List<BoardPageResponseDto> response = new ArrayList<>();

    for (CategoryDto category : categoryList) {
      response.add(
          BoardPageResponseDto.builder()
              .category(category)
              .boardList(boardService.getBoardList(pageable, category.getCategoryId(), boardType))
              .build());
    }
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{boardType}/category")
  public ResponseEntity<List<CategoryDto>> getCategoryList(
      @RequestParam("category") Long categoryId) {
    return ResponseEntity.ok(boardService.getCategoryList(categoryId));
  }

  @PostMapping("/{boardType}/category")
  public ResponseEntity<Boolean> registerCategory(
      @RequestParam("categoryName") String categoryName,
      @PathVariable("boardType") String boardType,
      @RequestHeader("authority") String authority) {
    return ResponseEntity.ok(boardService.registerCategory(categoryName, boardType, authority));
  }

  @PatchMapping("/{boardType}/category")
  public ResponseEntity<Boolean> modifyCategory(
      @RequestBody CategoryModifyRequestDto categoryModifyRequestDto,
      @RequestHeader("authority") String authority) {
    return ResponseEntity.ok(boardService.modifyCategory(categoryModifyRequestDto, authority));
  }

  @DeleteMapping("/{boardType}/category")
  public ResponseEntity<Boolean> deleteCategory(
      @RequestParam("categoryId") Long categoryId, @RequestHeader("authority") String authority) {
    return ResponseEntity.ok(boardService.deleteCategory(categoryId, authority));
  }

  @GetMapping("/{boardType}/search")
  public ResponseEntity<List<BoardPageResponseDto>> searchBoardList(
      @PathVariable("boardType") String boardType,
      @RequestParam("category") Long categoryId,
      @RequestParam("keyword") String keyword,
      @PageableDefault(size = 4, sort = "created_at,desc") Pageable pageable)
      throws Exception {

    List<CategoryDto> categoryList = boardService.getCategoryList(categoryId);
    List<BoardPageResponseDto> response = new ArrayList<>();
    for (CategoryDto category : categoryList) {
      response.add(
          BoardPageResponseDto.builder()
              .category(category)
              .boardList(boardService.searchBoardList(pageable, categoryId, keyword, boardType))
              .build());
    }
    return ResponseEntity.ok(response);
  }

  @GetMapping("{boardType}/detail/{boardId}")
  public ResponseEntity<BoardDetailResponseDto> getBoardDetail(
      @PathVariable("boardType") String boardType,
      @PathVariable("boardId") Long boardId,
      @RequestHeader("memberId") String memberId,
      @RequestHeader("authority") String authority)
      throws Exception {

    return ResponseEntity.ok(boardService.getBoardDetail(boardId, memberId, authority));
  }

  @PostMapping("/{boardType}")
  public ResponseEntity<Boolean> registerBoard(
      @RequestBody BoardRegisterRequestDto request,
      @RequestHeader("memberId") String memberId,
      @RequestHeader("memberName") String memberName)
      throws Exception {
    return ResponseEntity.ok(boardService.registerBoard(request, memberId, memberName));
  }

  @PutMapping("/{boardType}")
  public ResponseEntity<Boolean> modifyBoard(
      @RequestBody BoardModifyRequestDto request, @RequestHeader("memberId") String memberId)
      throws Exception {
    return ResponseEntity.ok(
        boardService.modifyBoard(request.toBuilder().memberId(memberId).build()));
  }

  @DeleteMapping("/{boardType}/{boardId}")
  public ResponseEntity<Boolean> removeBoard(
      @PathVariable("boardType") String boardType,
      @PathVariable("boardId") Long boardId,
      @RequestHeader("memberId") String memberId,
      @RequestHeader("authority") String authority)
      throws Exception {
    return ResponseEntity.ok(boardService.removeBoard(boardId, memberId, authority));
  }

  @GetMapping("/member")
  public ResponseEntity<Page<BoardResponseDto>> getMemberBoard(
      @PageableDefault(size = 3, sort = "created_at,desc") Pageable pageable,
      @RequestHeader("memberId") String memberId)
      throws Exception {
    return ResponseEntity.ok(boardService.getMemberBoard(pageable, memberId));
  }

  @GetMapping("/member/comment")
  public ResponseEntity<Page<CommentResponseDto>> getMemberComment(
      @PageableDefault(size = 3, sort = "created_at,desc") Pageable pageable,
      @RequestHeader("memberId") String memberId)
      throws Exception {
    return ResponseEntity.ok(boardService.getMemberComment(pageable, memberId));
  }

  @PostMapping("/comment")
  public ResponseEntity<Boolean> registerComment(
      @RequestBody CommentRegisterRequestDto request,
      @RequestHeader("memberId") String memberId,
      @RequestHeader("memberName") String memberName)
      throws Exception {
    return ResponseEntity.ok(boardService.registerComment(request, memberId, memberName));
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
      @RequestHeader("authority") String authority)
      throws Exception {
    return ResponseEntity.ok(boardService.removeComment(commentId, memberId, authority));
  }
}
