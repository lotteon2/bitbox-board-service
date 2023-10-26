package com.bitbox.board.dto.request;

import com.bitbox.board.entity.Board;
import com.bitbox.board.entity.Comment;
import java.awt.print.Pageable;
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
  @NotNull
  private Long boardId;

  @NotEmpty
  private String commentContents;

  @Nullable
  private Long masterCommentId;

  public Comment toEntity(Board board, String memberId, String memberName, String memberProfileImg) {
    return Comment.builder()
        .memberId(memberId)
        .memberName(memberName)
        .memberProfileImage(memberProfileImg)
        .board(board)
        .commentContents(commentContents)
        .build();
  }
}
