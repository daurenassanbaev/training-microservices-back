package com.daurenassanbaev.productservice.database.repository;

import com.daurenassanbaev.productservice.database.entity.Product;
import com.daurenassanbaev.productservice.dto.ProductFilter;

import java.util.List;

public interface FilterProductRepository {
    List<Product> findAllByFilter(ProductFilter filter);

}
