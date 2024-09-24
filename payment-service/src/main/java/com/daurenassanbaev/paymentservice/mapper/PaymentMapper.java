package com.daurenassanbaev.paymentservice.mapper;

import com.daurenassanbaev.paymentservice.database.entity.Payment;
import com.daurenassanbaev.paymentservice.dto.PaymentDto;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper implements Mapper<PaymentDto, Payment> {
    @Override
    public Payment map(PaymentDto object) {
        Payment order = new Payment();
        copy(object, order);
        return order;
    }
    private void copy(PaymentDto object, Payment payment) {
        payment.setAmount(object.getAmount());
        payment.setStatus(object.getStatus());
        payment.setCardId(object.getCartId());
    }
}
