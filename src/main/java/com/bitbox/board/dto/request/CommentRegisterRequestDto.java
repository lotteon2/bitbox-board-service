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
public class CommentRegisterRequestDto {

  @NotEmpty
  private String memberId;

  @NotEmpty
  private String memberName;

  @NotNull
  private Long boardId;

  @NotEmpty
  private String commentContents;

  @Nullable
  private Long masterCommentId;
}
