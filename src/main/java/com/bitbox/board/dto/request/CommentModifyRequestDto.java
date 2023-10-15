package com.bitbox.board.dto.request;

import javax.annotation.Nullable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentModifyRequestDto {

  @NotNull
  private Long commentId;
  @NotNull
  private Long boardId;
  @NotEmpty
  private String commentContents;
  @Nullable
  private Long masterCommentId;
}
