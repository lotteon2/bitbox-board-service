package com.bitbox.board.exception;

import lombok.Getter;

@Getter
public class AdminClassCreateFailException extends RuntimeException {
  private static final String message = "반 생성을 실패했습니다.";

  public AdminClassCreateFailException() {
    super(AdminClassCreateFailException.message);
  }
}
