package com.bitbox.board.dto.response;

import com.bitbox.board.entity.Board;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BoardResponseDto {

  private Long boardId;
  private String memberId;
  private String memberName;
  private String memberProfileImg;
  private String memberAuthority;
  private Long categoryId;
  private String categoryName;
  private String boardTitle;
  private String boardContents;
  private LocalDateTime createdAt;

  @JsonInclude(Include.NON_NULL)
  private Long masterCategoryId;

  @JsonInclude(Include.NON_NULL)
  private String authority;

  @JsonInclude(Include.NON_NULL)
  private boolean isDeleted;

  @JsonInclude(Include.NON_NULL)
  private LocalDateTime updatedAt;

  @JsonInclude(Include.NON_NULL)
  private String thumbnail;

  public BoardResponseDto(Board board) {
    this.boardId = board.getId();
    this.memberId = board.getMemberId();
    this.memberName = board.getMemberName();
    this.memberProfileImg = board.getMemberProfileImage();
    this.memberAuthority = board.getMemberAuthority();
    this.categoryId = board.getCategory().getId();
    this.categoryName = board.getCategory().getCategoryName();
    this.boardTitle = board.getBoardTitle();
    this.boardContents = board.getBoardContents();
    this.isDeleted = board.isDeleted();
    this.createdAt = board.getCreatedAt();
  }

  public void updateThumbnail(String thumbnail) {
    this.thumbnail = thumbnail;
  }

  public void updateMasterCategoryId(Long masterCategoryId) {
    this.masterCategoryId = masterCategoryId;
  }
}
