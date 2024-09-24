package com.daurenassanbaev.productservice.database.repository;

import com.daurenassanbaev.productservice.database.entity.Product;
import com.daurenassanbaev.productservice.dto.ProductFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>, FilterProductRepository{
}
