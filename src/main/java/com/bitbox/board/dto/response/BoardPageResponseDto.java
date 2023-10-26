package com.bitbox.board.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardPageResponseDto {
  Page<BoardResponseDto> boardList;
  CategoryDto category;
}
