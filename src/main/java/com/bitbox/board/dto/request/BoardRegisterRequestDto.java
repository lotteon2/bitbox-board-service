package com.bitbox.board.dto.request;

import com.bitbox.board.entity.Board;
import com.bitbox.board.entity.Category;
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
public class BoardRegisterRequestDto {
  @NotNull
  private Long categoryId;

  @NotEmpty
  private String boardTitle;

  @NotEmpty
  private String boardContents;

  @Nullable
  private List<MultipartFile> images;

  public Board toEntity(Category category, String memberId, String memberName) {
    return Board.builder()
        .boardTitle(boardTitle)
        .boardContents(boardContents)
        .category(category)
        .memberId(memberId)
        .memberName(memberName)
        .build();
  }
}
