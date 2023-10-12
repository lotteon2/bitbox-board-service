package com.bitbox.board.dto;

import com.bitbox.board.entity.Board;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardResponseDto {
  private Long boardId;
  private String memberId;
  private String boardTitle;
  private String boardContents;

  @JsonInclude(Include.NON_NULL)
  private String thumbnail;

  public BoardResponseDto(Board board) {
    this.boardId = board.getId();
    this.memberId = board.getMemberId();
    this.boardTitle = board.getBoardTitle();
    this.boardContents = board.getBoardContents();
  }
}
