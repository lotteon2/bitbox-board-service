package com.bitbox.board.dto.request;

import java.util.List;
import javax.annotation.Nullable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BoardModifyRequestDto {

  @NotEmpty
  private String memberId;

  @NotNull
  private Long boardId;

  @NotNull
  private Long categoryId;

  @NotEmpty
  private String boardTitle;

  @NotEmpty
  private String boardContents;

  @Nullable
  private String thumbnail;
}
