package com.daurenassanbaev.productservice.controller;

import com.daurenassanbaev.productservice.database.entity.Product;
import com.daurenassanbaev.productservice.dto.ProductDto;
import com.daurenassanbaev.productservice.dto.ProductFilter;
import com.daurenassanbaev.productservice.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(required = false) int page, @RequestParam(required = false) int size) {
        if (page < 0 || size <= 0) {
            return ResponseEntity.badRequest().body("Page number must be 0 or greater, size must be positive.");
        }
        var page1 = productService.findAll(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(page1);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Product>> findAll(@RequestParam(required = false) String name,
                                                    @RequestParam(required = false) BigDecimal minPrice,
                                                    @RequestParam(required = false) BigDecimal maxPrice) {
        ProductFilter productFilter = new ProductFilter(name, minPrice, maxPrice);
        List<Product> list = productService.findAll(productFilter);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @GetMapping("/{id}")
    public ProductDto findById(@PathVariable("id") Integer id) {
        return productService.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @Validated
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDto> create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ProductDto productDto) {
        var roles = (List<String>) jwt.getClaimAsMap("realm_access").get("roles");
        var res1 = jwt.getClaim("preferred_username");
        var res = productService.create(productDto);
        if (roles.contains("ROLE_ADMIN")) {

        }
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ProductDto update(@PathVariable("id") Integer id, @RequestBody ProductDto productDto) {
        return productService.update(id, productDto).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable("id") Integer id) {
        if (!productService.delete(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }




}
