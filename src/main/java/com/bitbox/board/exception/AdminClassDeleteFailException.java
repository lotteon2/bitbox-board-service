package com.bitbox.board.exception;

import lombok.Getter;

@Getter
public class AdminClassDeleteFailException extends RuntimeException {
  private static final String message = "반 삭제를 실패했습니다.";

  public AdminClassDeleteFailException() {
    super(AdminClassDeleteFailException.message);
  }
}
