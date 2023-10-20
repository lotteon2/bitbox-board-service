package com.bitbox.board.exception;

import lombok.Getter;

@Getter
public class NotPermissionException extends RuntimeException {
  private static final String messsage = "권한이 없습니다.";
  public NotPermissionException() {
    super(NotPermissionException.messsage);
  }
}
