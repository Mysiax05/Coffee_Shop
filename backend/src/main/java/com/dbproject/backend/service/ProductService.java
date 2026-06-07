package com.dbproject.backend.service;

import com.dbproject.backend.dto.ProductDto;
import com.dbproject.backend.dto.ProductFilterRequest;
import com.dbproject.backend.entity.Product;
import com.dbproject.backend.exception.ResourceNotFoundException;
import com.dbproject.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<ProductDto> getAll() {
        return productRepository.findAllActive()
                .stream()
                .filter(Product::getIsActive)
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ProductDto findActiveById(Integer productId) {
        Product product = productRepository.findById(productId)
                .filter(Product::getIsActive)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Product with ID %d was not found", productId)));
        return toDto(product);
    }

    public List<ProductDto> filterProducts(ProductFilterRequest request) {
        List<Object[]> rows = productRepository.filterProducts(
                request.getMinPrice(),
                request.getMaxPrice(),
                request.getCategoryId(),
                request.getAttributes()
        );

        return rows.stream().map(row -> {
            ProductDto dto = new ProductDto();
            dto.setProductId((Integer) row[0]);
            dto.setName((String) row[1]);
            dto.setPrice((BigDecimal) row[2]);
            dto.setStock((Integer) row[3]);
            dto.setAttributes(row[4] != null ? row[4].toString() : "{}");
            return dto;
        }).toList();
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

    public void deactivateProduct(Integer id) {
        productRepository.deactivateProduct(id);
    }

    public void updateProductPrice(Integer id, BigDecimal newPrice) { productRepository.updateProductPrice(id,newPrice);}

    public void addProductStock(Integer id, Integer stockToAdd) { productRepository.addProductStock(id, stockToAdd);}
}
