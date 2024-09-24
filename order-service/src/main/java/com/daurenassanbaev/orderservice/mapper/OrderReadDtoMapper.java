package com.daurenassanbaev.orderservice.mapper;

import com.daurenassanbaev.orderservice.database.entity.Order;
import com.daurenassanbaev.orderservice.dto.OrderReadDto;
import org.springframework.stereotype.Component;

@Component
public class OrderReadDtoMapper implements Mapper<Order, OrderReadDto> {
    @Override
    public OrderReadDto map(Order object) {
        OrderReadDto orderReadDto = new OrderReadDto();
        copy(object, orderReadDto);
        return orderReadDto;
    }
    private void copy(Order object, OrderReadDto order) {
        order.setQuantity(object.getQuantity());
        order.setTotalPrice(object.getTotalPrice());
        order.setUserId(object.getUserId());
        order.setProductId(object.getProductId());
        order.setStatus(order.getStatus());
    }
}
