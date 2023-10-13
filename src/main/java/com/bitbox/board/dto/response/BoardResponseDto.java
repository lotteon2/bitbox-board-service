package com.bitbox.board.dto.response;

import com.bitbox.board.entity.Board;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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
  private String memeberName;
  private String boardTitle;
  private String boardContents;
  private boolean isDeleted;
  private LocalDateTime createdAt;

  @JsonInclude(Include.NON_NULL)
  private LocalDateTime updatedAt;

  @JsonInclude(Include.NON_NULL)
  private String thumbnail;

  public BoardResponseDto(Board board) {
    this.boardId = board.getId();
    this.memberId = board.getMemberId();
    this.memeberName = board.getMemberName();
    this.boardTitle = board.getBoardTitle();
    this.boardContents = board.getBoardContents();
    this.isDeleted = board.isDeleted();
    this.createdAt = board.getCreatedAt();
  }
}
