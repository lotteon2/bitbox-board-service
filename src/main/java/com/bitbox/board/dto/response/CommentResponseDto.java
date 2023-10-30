package com.bitbox.board.dto.response;

import com.bitbox.board.entity.Comment;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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

  private List<CommentResponseDto> commentList;

  private String memberId;

  private String memberName;

  private String memberProfileImage;

  private String commentContents;

  private boolean isManagement = false;

  private LocalDateTime createdAt;

  public CommentResponseDto(Comment comment) {
    this.commentId = comment.getId();
    this.boardId = comment.getBoard().getId();
    this.commentList = comment.getCommentList().stream().map(CommentResponseDto::new).collect(
        Collectors.toList());
    this.memberId = comment.getMemberId();
    this.memberName = comment.getMemberName();
    this.commentContents = comment.getCommentContents();
    this.createdAt = comment.getCreatedAt();
    this.memberProfileImage = comment.getMemberProfileImage();
    this.masterCommentId = Optional.ofNullable(comment.getMasterComment())
        .map(Comment::getId)
        .orElse(-1L);
    this.isManagement = comment.isManagement();
  }
}
