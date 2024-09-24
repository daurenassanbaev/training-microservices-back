package com.daurenassanbaev.productservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    @NotBlank(message = "Name must not be null")
    @Size(min = 1, message = "Name must not be empty")
    private String name;

    private String description;

    @NotNull(message = "Price must not be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Quantity must not be null")
    @Min(value = 0, message = "Quantity must be greater than or equal to 0")
    private Integer quantity;
}
