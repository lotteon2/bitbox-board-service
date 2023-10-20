package com.bitbox.board.repository;

import com.bitbox.board.entity.Category;
import com.bitbox.board.entity.ClassCategory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassCategoryRepository extends JpaRepository<ClassCategory, Category> {
  Optional<ClassCategory> findByClassId(Long classId);
}
