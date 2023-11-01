package com.bitbox.board.exception;

import lombok.Getter;

@Getter
public class BoardTypeMissmatchException extends RuntimeException {
  private static final String message = "게시글 경로와 카테고리 타입이 맞지 않습니다.";

  public BoardTypeMissmatchException() {
    super(BoardTypeMissmatchException.message);
  }
}
