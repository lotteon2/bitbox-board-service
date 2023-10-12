package com.bitbox.board.dto.response;

import com.bitbox.board.dto.CategoryDto;
import com.bitbox.board.dto.CommentDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BoardDetailResponseDto {
  private CategoryDto category;
  private BoardResponseDto boardResponse;
  private List<CommentDto> commentList;
  private boolean isManagement;
}
