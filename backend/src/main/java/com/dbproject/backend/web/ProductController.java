package com.dbproject.backend.web;

import com.dbproject.backend.dto.ProductDto;
import com.dbproject.backend.dto.ProductFilterRequest;
import com.dbproject.backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

    @PostMapping("/filter")
    public ResponseEntity<List<ProductDto>> filterProducts(@RequestBody ProductFilterRequest request) {
        return ResponseEntity.ok(productService.filterProducts(request));
    }

    @PatchMapping("/{id}/updatePrice")
    public ResponseEntity<Void> updateProductPrice(@PathVariable Integer id, @RequestParam BigDecimal newPrice){
        productService.updateProductPrice(id, newPrice);
        return ResponseEntity.status(200).build();
    }

    @PatchMapping("/{id}/addStock")
    public ResponseEntity<Void> addProductStock(@PathVariable Integer id, @RequestParam Integer stockToAdd){
        productService.addProductStock(id, stockToAdd);
        return ResponseEntity.status(200).build();
    }
}
