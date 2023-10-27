package com.bitbox.board.dto.request;

import javax.annotation.Nullable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CommentModifyRequestDto {
  @NotNull
  private Long commentId;

  @NotEmpty
  private String commentContents;
}
