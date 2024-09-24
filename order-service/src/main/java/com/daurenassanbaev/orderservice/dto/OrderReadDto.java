package com.daurenassanbaev.orderservice.dto;

import com.daurenassanbaev.orderservice.database.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderReadDto {
    private String userId;
    private Integer productId;
    private Integer quantity;
    private BigDecimal totalPrice;
    private Status status;
}
