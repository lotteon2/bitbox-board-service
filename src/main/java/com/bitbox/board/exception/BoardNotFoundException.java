package com.bitbox.board.exception;

import lombok.Getter;

@Getter
public class BoardNotFoundException extends RuntimeException {
  private static final String message = "게시글을 찾을 수 없습니다.";

  public BoardNotFoundException() {
    super(BoardNotFoundException.message);
  }
}
