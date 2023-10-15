package com.bitbox.board.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Builder(toBuilder = true)
@DynamicInsert
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "board_id")
  private Long id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "fk_category_to_board"))
  private Category category;

  @OneToMany(mappedBy = "board")
  private List<Comment> comments = new ArrayList<>();

  @NotNull
  @Column(name = "member_id")
  private String memberId;

  @NotNull
  @Column(name = "member_name")
  private String memberName;

  @NotNull
  @Column(name = "board_title")
  private String boardTitle;

  @NotNull
  @Column(name = "board_contents")
  private String boardContents;

  @NotNull
  @Column(name = "is_deleted", columnDefinition = "boolean default false")
  private boolean isDeleted;
}
