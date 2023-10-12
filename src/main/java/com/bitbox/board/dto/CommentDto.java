package com.bitbox.board.dto;

import com.bitbox.board.entity.Comment;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
  private Long commentId;
  private Long masterCommentId;
  private String memberId;
  private String commentContents;
  private LocalDateTime createdAt;

  public CommentDto(Comment comment) {
    this.commentId = comment.getId();
    this.masterCommentId = comment.getMasterComment().getId();
    this.memberId = comment.getMemberId();
    this.commentContents = comment.getCommentContents();
    this.createdAt = comment.getCreatedAt();
  }
}
