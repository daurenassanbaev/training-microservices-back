package com.daurenassanbaev.paymentservice.mapper;

import com.daurenassanbaev.paymentservice.database.entity.Payment;
import com.daurenassanbaev.paymentservice.dto.PaymentDto;
import org.springframework.stereotype.Component;

@Component
public class PaymentDtoMapper implements Mapper<Payment, PaymentDto> {
    @Override
    public PaymentDto map(Payment object) {
        PaymentDto paymentDto = new PaymentDto();
        copy(object, paymentDto);
        return paymentDto;
    }
    private void copy(Payment object, PaymentDto payment) {
        payment.setAmount(object.getAmount());
        payment.setStatus(object.getStatus());
        payment.setCartId(object.getCardId());
    }
}
