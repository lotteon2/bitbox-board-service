package com.bitbox.board.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardListResponseDto {
  private Long categoryId;
  private String categoryName;
  private List<BoardResponseDto> boardResponseDtoList;
}
