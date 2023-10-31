package com.bitbox.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BoardType {
  DEVLOG("데브로그"),
  COMMUNITY("커뮤니티"),
  SENIOR("선배들의 이야기"),
  ALUMNI("알럼나이");

  private String category;

  public static String findByCategory(String category) {
    for (BoardType boardType : BoardType.values()) {
      if (boardType.toString().equals(category)) {
        return boardType.getCategory();
      }
    }
    throw new RuntimeException();
  }
}
