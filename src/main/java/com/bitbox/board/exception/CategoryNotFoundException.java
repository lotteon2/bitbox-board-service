package com.bitbox.board.exception;

import lombok.Getter;

@Getter
public class CategoryNotFoundException extends RuntimeException {
  private static final String message = "카테고리를 찾을 수 없습니다.";

  public CategoryNotFoundException() {
    super(CategoryNotFoundException.message);
  }
}
