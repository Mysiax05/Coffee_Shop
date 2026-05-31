package com.dbproject.backend.service;

import com.dbproject.backend.dto.ProductDto;
import com.dbproject.backend.entity.Product;
import com.dbproject.backend.exception.ResourceNotFoundException;
import com.dbproject.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<ProductDto> getAll() {
        return productRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ProductDto findById(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Product with ID %d was not found", productId)));
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
