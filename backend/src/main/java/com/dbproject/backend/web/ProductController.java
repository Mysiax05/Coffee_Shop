package com.dbproject.backend.web;

import com.dbproject.backend.dto.ProductDto;
import com.dbproject.backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllActive() {
        return ResponseEntity.ok(productService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> findActiveById(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.findActiveById(id));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateProduct(@PathVariable Integer id){
        productService.deactivateProduct(id);
        return ResponseEntity.status(200).build();
    }

}
