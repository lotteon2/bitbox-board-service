package com.bitbox.board.entity;

import com.sun.istack.NotNull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;

@Entity
@Getter
public class Comment extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long commentId;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "board_id", foreignKey = @ForeignKey(name = "fk_board_to_category"))
  private Board board;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "master_comment_id", foreignKey = @ForeignKey(name = "fk_master_comment_to_comment"))
  private Comment masterComment;

  @NotNull
  @Column(name = "member_id")
  private String memberId;

  @NotNull
  @Column(name = "comment_contents")
  private String commentContents;

  @NotNull
  @Column(name = "is_deleted")
  private boolean isDeleted;
}
