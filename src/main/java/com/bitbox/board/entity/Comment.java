package com.bitbox.board.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Comment extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "comment_id")
  private Long id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "board_id", foreignKey = @ForeignKey(name = "fk_board_to_comment"))
  private Board board;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "master_comment_id", foreignKey = @ForeignKey(name = "fk_master_comment_to_comment"))
  private Comment masterComment;

  @NotNull
  @Column(name = "member_id")
  private String memberId;

  @NotNull
  @Column(name = "member_name")
  private String memberName;

  @NotNull
  @Column(name = "comment_contents")
  private String commentContents;

  @NotNull
  @Column(name = "is_deleted", columnDefinition = "boolean default false")
  private boolean isDeleted;
}
