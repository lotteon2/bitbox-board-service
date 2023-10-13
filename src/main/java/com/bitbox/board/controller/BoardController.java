package com.bitbox.board.controller;

import com.bitbox.board.dto.response.BoardListResponseDto;
import com.bitbox.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

  private final BoardService boardService;

  @GetMapping
  public String test() {
    return "Test";
  }

  @GetMapping("/{boardType}")
  public ResponseEntity<BoardListResponseDto> getBoardList(
      @PathVariable("boardType") String boardType,
      @RequestParam(value = "category") Long categoryId,
      @PageableDefault(size = 5) Pageable pageable) {



    return ResponseEntity.ok(null);
  }
}