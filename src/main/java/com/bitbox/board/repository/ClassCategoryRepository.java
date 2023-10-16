package com.bitbox.board.repository;

import com.bitbox.board.entity.Category;
import com.bitbox.board.entity.ClassCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassCategoryRepository extends JpaRepository<ClassCategory, Category> {

}
