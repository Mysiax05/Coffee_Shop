package com.dbproject.backend.service;

import com.dbproject.backend.dto.CategoryDto;
import com.dbproject.backend.entity.Category;
import com.dbproject.backend.exception.ResourceNotFoundException;
import com.dbproject.backend.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<CategoryDto> getAll() {
        return categoryRepository
                .findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<CategoryDto> getCategorySubtree(Integer categoryId) {
        List<Integer> ids = categoryRepository.getCategorySubtreeIds(categoryId);
        return categoryRepository.findAllById(ids)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public CategoryDto toDto(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryId(category.getCategoryId());
        categoryDto.setCategoryName(category.getCategoryName());
        if (category.getParentCategory() != null) {
            categoryDto.setParentCategoryId(category.getParentCategory().getCategoryId());
            categoryDto.setParentCategoryName(category.getParentCategory().getCategoryName());
        }
        return categoryDto;
    }
}
