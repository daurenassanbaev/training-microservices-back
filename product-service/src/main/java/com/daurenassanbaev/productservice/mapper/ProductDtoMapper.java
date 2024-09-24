package com.daurenassanbaev.productservice.mapper;

import com.daurenassanbaev.productservice.database.entity.Product;
import com.daurenassanbaev.productservice.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductDtoMapper implements Mapper<Product, ProductDto> {
    @Override
    public ProductDto map(Product object) {
        ProductDto product = new ProductDto();
        copy(object, product);
        return product;
    }
    private void copy(Product object, ProductDto product) {
        product.setDescription(object.getDescription());
        product.setName(object.getName());
        product.setPrice(object.getPrice());
        product.setQuantity(object.getQuantity());
    }
    public Page<ProductDto> mapPage(Page<Product> productPage) {
        List<ProductDto> productDtos = productPage.getContent().stream().map(this::map).toList();
        return new PageImpl<>(productDtos, productPage.getPageable(), productPage.getTotalElements());
    }
}
