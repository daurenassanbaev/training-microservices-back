package com.daurenassanbaev.cartservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    private Integer orderId;
    private Integer cartId;
    private Integer id;
}
