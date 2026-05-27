package com.dbproject.backend.service;

import com.dbproject.backend.dto.ProductDto;
import com.dbproject.backend.entity.Product;
import com.dbproject.backend.exception.ResourceNotFoundException;
import com.dbproject.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public ProductDto findById(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product with ID % does not exist"));
        return toDto(product);
    }

    public ProductDto toDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setCategoryId(product.getCategory().getCategoryId());
        dto.setCategoryName(product.getCategory().getCategoryName());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setActive(product.getIsActive());
        dto.setAttributes(product.getAttributes());
        return dto;
    }

}
