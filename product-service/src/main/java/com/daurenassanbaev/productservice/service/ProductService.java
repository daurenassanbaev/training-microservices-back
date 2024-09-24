package com.daurenassanbaev.productservice.service;

import com.daurenassanbaev.productservice.database.entity.Product;
import com.daurenassanbaev.productservice.database.repository.ProductRepository;
import com.daurenassanbaev.productservice.dto.ProductDto;
import com.daurenassanbaev.productservice.dto.ProductFilter;
import com.daurenassanbaev.productservice.mapper.ProductDtoMapper;
import com.daurenassanbaev.productservice.mapper.ProductMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Transactional(readOnly = true)
@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductDtoMapper productDtoMapper;
    private final KafkaTemplate<String, Integer> kafkaTemplate;
    private final KafkaTemplate<String, List<Integer>> listKafkaTemplate;

    @Transactional
    public ProductDto create(ProductDto productDto) {
        productRepository.save(productMapper.map(productDto));
        return productDto;
    }

    public Page<ProductDto> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> product = productRepository.findAll(pageable);
        return productDtoMapper.mapPage(product);
    }

    public List<Product> findAll(ProductFilter productFilter) {
        List<Product> products = productRepository.findAllByFilter(productFilter);
        return products;
    }


    public Optional<ProductDto> findById(Integer id) {
        Optional<Product> product = productRepository.findById(id);
        return product.map(productDtoMapper::map);
    }
    @Transactional
    public Optional<ProductDto> update(Integer id, ProductDto productDto) {
        Optional<Product> foundProduct = productRepository.findById(id);
        if (foundProduct.isPresent()) {
            Product product1 = foundProduct.get();
            product1.setId(id);
            product1.setDescription(productDto.getDescription());
            product1.setName(productDto.getName());
            product1.setPrice(productDto.getPrice());
            product1.setQuantity(productDto.getQuantity());
            Product product = productRepository.save(product1);
            return Optional.of(productDtoMapper.map(product));
        }
        return Optional.empty();
    }

    @KafkaListener(topics = {"reduce-product"})
    @KafkaHandler
    @Transactional
    public void reduceQuantity(Map<String, String> map) {
        Integer productId = Integer.parseInt(map.get("productId"));
        Integer quantity = Integer.parseInt(map.get("quantity"));
        Optional<Product> foundProduct = productRepository.findById(productId);
        if (foundProduct.isPresent()) {
            Product product1 = foundProduct.get();
            if (product1.getQuantity() > quantity) {
                product1.setQuantity(product1.getQuantity() - quantity);
                productRepository.save(product1);
            } else if (product1.getQuantity().equals(quantity)) {
                delete(productId);
            } else {
                listKafkaTemplate.send("order-errors", List.of(1));
            }
        }
    }

    @Transactional
    public boolean delete(Integer id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
