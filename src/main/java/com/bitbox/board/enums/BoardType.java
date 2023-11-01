package com.bitbox.board.enums;

import com.bitbox.board.exception.BoardTypeMissmatchException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BoardType {
  DEVLOG("데브로그"),
  COMMUNITY("커뮤니티"),
  SENIOR("선배들의 이야기"),
  ALUMNI("알럼나이");

  private final String category;

  public static String findByCategory(String category) {
    for (BoardType boardType : BoardType.values()) {
      if (boardType.toString().toLowerCase().equals(category)) {
        return boardType.getCategory();
      }
    }
    throw new BoardTypeMissmatchException();
  }

  public static String findByCategoryName(String categoryName) {
    for (BoardType boardType : BoardType.values()) {
      if (boardType.getCategory().equals(categoryName)) {
        return boardType.toString().toLowerCase();
      }
    }
    throw new BoardTypeMissmatchException();
  }
}
