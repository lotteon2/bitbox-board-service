package com.bitbox.board.dto.response;

import com.bitbox.board.entity.Comment;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {

  private Long commentId;

  private Long boardId;

  @JsonInclude(Include.NON_NULL)
  private Long masterCommentId;

  private String memberId;

  private String memberProfileImage;

  private String commentContents;

  private LocalDateTime createdAt;

  public CommentResponseDto(Comment comment) {
    this.commentId = comment.getId();
    this.boardId = comment.getBoard().getId();
    this.memberId = comment.getMemberId();
    this.commentContents = comment.getCommentContents();
    this.createdAt = comment.getCreatedAt();
    this.memberProfileImage = comment.getMemberProfileImage();
    this.masterCommentId = Optional.ofNullable(comment.getMasterComment())
        .map(Comment::getId)
        .orElse(-1L);
  }
}
