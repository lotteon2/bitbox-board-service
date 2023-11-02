package com.bitbox.board.exception;

import lombok.Getter;

@Getter
public class BoardUnauthorizedException extends RuntimeException {
  private static final String message = "게시판 접근 권한이 없습니다.";

  public BoardUnauthorizedException() {
    super(BoardUnauthorizedException.message);
  }
}
