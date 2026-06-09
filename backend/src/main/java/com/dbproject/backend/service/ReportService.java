package com.dbproject.backend.service;

import com.dbproject.backend.dto.BestSellerDto;
import com.dbproject.backend.entity.Category;
import com.dbproject.backend.repository.CategoryRepository;
import com.dbproject.backend.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ReportService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<BestSellerDto> getTopBestSellers() {
        return mapToBestSellerDto(productRepository.findBestSellers(3, null));
    }

    public Map<String, BestSellerDto> getBestSellerPerCategory() {
        List<Category> categories = categoryRepository.findAll();
        Map<String, BestSellerDto> result = new HashMap<>();

        for (Category category : categories) {
            List<Object[]> rows = productRepository.findBestSellers(1, category.getCategoryId());
            if (!rows.isEmpty()) {
                result.put(category.getCategoryName(), mapToBestSellerDto(rows).get(0));
            }
        }
        return result;
    }

    private List<BestSellerDto> mapToBestSellerDto(List<Object[]> rows) {
        return rows.stream().map(row -> {
            BestSellerDto dto = new BestSellerDto();
            dto.setProductId((Integer) row[0]);
            dto.setName((String) row[1]);
            dto.setTotalSold((Integer) row[2]);
            dto.setRevenue((BigDecimal) row[3]);
            return dto;
        }).toList();
    }
}