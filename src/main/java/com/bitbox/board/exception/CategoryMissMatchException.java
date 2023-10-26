package com.bitbox.board.exception;

import lombok.Getter;

@Getter
public class CategoryMissMatchException extends RuntimeException {
  private static final String message = "카테고리 구분이 맞지 않습니다.";

  public CategoryMissMatchException() {
    super(CategoryMissMatchException.message);
  }
}
