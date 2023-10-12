package com.bitbox.board.dto.response;

import com.bitbox.board.dto.CategoryDto;
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
  private CategoryDto category;
  private List<BoardResponseDto> boardList;
}
