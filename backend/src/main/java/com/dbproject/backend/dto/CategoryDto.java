package com.dbproject.backend.dto;

import lombok.Data;

@Data
public class CategoryDto {
    private Integer categoryId;
    private String categoryName;
    private Integer parentCategoryId;
    private String parentCategoryName;
}
