package com.bitbox.board.dto.response;

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
  private BoardResponseDto boardResponse;
  private List<CommentResponseDto> commentList;
  private List<String> imgList;
  private boolean isManagement;
}
