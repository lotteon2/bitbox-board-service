package com.bitbox.board.dto;

import com.bitbox.board.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
  private Long categoryId;
  private String categoryName;

  public CategoryDto(Category category) {
    this.categoryId = category.getId();
    this.categoryName = category.getCategoryName();
  }
}
