package com.bitbox.board.exception;

import lombok.Getter;

@Getter
public class CommentNotFoundException extends RuntimeException {
  private static final String message = "댓글을 찾을 수 없습니다.";

  public CommentNotFoundException() {
    super(CommentNotFoundException.message);
  }
}
