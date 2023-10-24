package com.bitbox.board.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.bitbox.board.vo.BoardImageId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@DynamoDBTable(tableName = "board_image")
public class BoardImage {

  @Id
  private BoardImageId boardImageId;

  @DynamoDBHashKey(attributeName = "board_id")
  public Long getBoardId() {
    return boardImageId != null ? boardImageId.getBoardId() : null;
  }

  public void setBoardId(Long boardId) {
    if (boardImageId == null) {
      boardImageId = new BoardImageId();
    }
    boardImageId.setBoardId(boardId);
  }

  @DynamoDBRangeKey(attributeName = "timestamp")
  public String getTimestamp() {
    return boardImageId != null ? boardImageId.getTimestamp() : null;
  }

  public void setTimestamp(String timestamp) {
    if (boardImageId == null) {
      boardImageId = new BoardImageId();
    }
    boardImageId.setTimestamp(timestamp);
  }

  @DynamoDBAttribute(attributeName = "img_url")
  private String imgUrl;
}
