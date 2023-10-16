package com.bitbox.board.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
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
@NoArgsConstructor
public class ClassCategory {

  @Id
  private Long categoryId;

  @OneToOne
  @MapsId
  @JoinColumn(name = "category_id")
  private Category category;

  @NotNull
  @Column(name = "class_id")
  private Long classId;

}
