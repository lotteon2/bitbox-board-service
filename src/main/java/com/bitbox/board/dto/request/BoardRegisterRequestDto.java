package com.bitbox.board.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardRegisterRequestDto {
  private Long categoryId;
  private String boardTitle;
  private String boardContents;
}
