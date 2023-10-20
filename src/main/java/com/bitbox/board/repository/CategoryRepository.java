package com.bitbox.board.repository;

import com.bitbox.board.entity.Category;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

  Optional<Category> findByCategoryName(String categoryName);
  List<Category> findByMasterCategory_Id(Long id);
}
