package com.dbproject.backend.repository;

import com.dbproject.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query(value = "SELECT categoryid FROM f_get_category_subtree(:categoryId)", nativeQuery = true)
    List<Integer> getCategorySubtreeIds(@Param("categoryId") Integer categoryId);
}
