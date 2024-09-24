package com.daurenassanbaev.productservice.mapper;


import com.daurenassanbaev.productservice.database.entity.Product;
import com.daurenassanbaev.productservice.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductMapper implements Mapper<ProductDto, Product> {
    @Override
    public Product map(ProductDto object) {
        Product product = new Product();
        copy(object, product);
        return product;
    }
    private void copy(ProductDto object, Product product) {
        product.setDescription(object.getDescription());
        product.setName(object.getName());
        product.setPrice(object.getPrice());
        product.setQuantity(object.getQuantity());
    }
}
