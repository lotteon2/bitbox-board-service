package com.bitbox.board.dto.request;

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
public class BoardRegisterRequestDto {

  @NotEmpty
  private String memberId;

  @NotEmpty
  private String memberName;

  @NotNull
  private Long categoryId;

  @NotEmpty
  private String boardTitle;

  @NotEmpty
  private String boardContents;
}
