package com.bitbox.board.dto.request;

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
public class BoardRegisterRequestDto {

  @NotNull
  private Long categoryId;

  @NotEmpty
  private String boardTitle;

  @NotEmpty
  private String boardContents;
}
