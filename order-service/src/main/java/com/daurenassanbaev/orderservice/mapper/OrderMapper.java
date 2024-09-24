package com.daurenassanbaev.orderservice.mapper;

import com.daurenassanbaev.orderservice.database.entity.Order;
import com.daurenassanbaev.orderservice.database.entity.Status;
import com.daurenassanbaev.orderservice.dto.OrderDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OrderMapper implements Mapper<OrderDto, Order> {
    @Override
    public Order map(OrderDto object) {
        Order order = new Order();
        copy(object, order);
        return order;
    }
    private void copy(OrderDto object, Order order) {
        order.setQuantity(object.getQuantity());
        order.setTotalPrice(object.getTotalPrice());
        order.setUserId(object.getUserId());
        order.setProductId(object.getProductId());
        order.setStatus(Status.PENDING);
        order.setOrderDate(LocalDateTime.now());
    }
}
