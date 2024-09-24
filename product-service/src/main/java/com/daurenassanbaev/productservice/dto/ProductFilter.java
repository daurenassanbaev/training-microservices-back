package com.daurenassanbaev.productservice.dto;

import java.math.BigDecimal;

public record ProductFilter(String name, BigDecimal minPrice, BigDecimal maxPrice) {
}
