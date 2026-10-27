// src/main/java/.../repository/CategoryRepository.java
package com.umc.study_week3.repository;

import com.umc.study_week3.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
