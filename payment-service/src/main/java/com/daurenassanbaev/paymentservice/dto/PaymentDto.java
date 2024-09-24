package com.daurenassanbaev.paymentservice.dto;

import com.daurenassanbaev.paymentservice.database.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private Integer cartId;
    private Status status;
    private BigDecimal amount;
}
