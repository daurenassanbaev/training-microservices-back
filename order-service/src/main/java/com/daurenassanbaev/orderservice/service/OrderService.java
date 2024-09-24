package com.daurenassanbaev.orderservice.service;

import com.daurenassanbaev.orderservice.database.entity.Order;
import com.daurenassanbaev.orderservice.database.repository.OrderRepository;
import com.daurenassanbaev.orderservice.dto.OrderDto;
import com.daurenassanbaev.orderservice.dto.OrderReadDto;
import com.daurenassanbaev.orderservice.mapper.OrderMapper;
import com.daurenassanbaev.orderservice.mapper.OrderReadDtoMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Transactional(readOnly = true)
@Service
@Slf4j
@RequiredArgsConstructor

public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderReadDtoMapper orderReadDtoMapper;
    private final KafkaTemplate<String, Map<String, String>> kafkaTemplate;
    private final KafkaTemplate<String, String> strkafkaTemplate;
    private final KafkaTemplate<String, BigDecimal> amountKafkaTemplate;
    private boolean hasError = false;
    private Integer cartId = -1;


    @Transactional
    public void create(OrderDto orderDto) throws Exception {
        var productId = orderDto.getProductId();
        var quantity = orderDto.getQuantity();
        Map<String, String> map = new HashMap<>();
        map.put("productId", ""+productId);
        map.put("quantity", ""+quantity);
        var res1 = kafkaTemplate.send("reduce-product", map).get();
        if (hasError) {
            throw new Exception("Quantity is more");
        } else {
            var res = orderRepository.save(orderMapper.map(orderDto));
            map.clear();
            // TODO
            map.put("userId", orderDto.getUserId());
            var res2 = kafkaTemplate.send("get-cart-id", map);
            //
            while (cartId == -1) {
                Thread.sleep(500);
            }
            map.clear();
            map.put("cartId", ""+cartId);
            hasError = false;
            cartId = -1;
            map.put("orderId", ""+res.getId());
            kafkaTemplate.send("create-cart-item", map).get();
            map.clear();
        }

    }
    @KafkaListener(topics = {"order-errors"})
    @KafkaHandler
    public void check(byte[] bytes) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Integer> integers = objectMapper.readValue(bytes, new TypeReference<List<Integer>>() {});

        if (integers.get(0) == 1) {
            hasError = true;
        }
    }
    @KafkaListener(topics = {"get-orders-amount"})
    @KafkaHandler
    @Transactional
    public void getAmounts(byte[] bytes) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> list = objectMapper.readValue(bytes, new TypeReference<List<String>>() {});
        List<Integer> res = new ArrayList<>();
        for (String str: list) {
            str = str.replace("[", "");
            str = str.replace("]", "");
            res.add(Integer.parseInt(str));
        }

        BigDecimal bigDecimal = BigDecimal.ZERO;
        for (Integer integer : res) {
            var d = findById(integer).get().getTotalPrice();
            bigDecimal = bigDecimal.add(d);
        }
        var id1 = "";
        for (Integer id : res) {
            Optional<Order> order = orderRepository.findById(id);
            if (id1.equals("")) {
                id1 = order.get().getUserId();
            }
            orderRepository.deleteById(id);
        }
        amountKafkaTemplate.send("total-amount", bigDecimal);
        strkafkaTemplate.send("reduce-balance-user", id1);
    }
    @KafkaListener(topics = {"give-cart-id"})
    @KafkaHandler
    public void getCartId(byte[] bytes) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Integer> list = objectMapper.readValue(bytes, new TypeReference<List<Integer>>() {});
        this.cartId = list.get(0);
    }

    public List<OrderReadDto> findAll() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(orderReadDtoMapper::map).toList();
    }

    public Optional<OrderReadDto> findById(Integer id) {
        Optional<Order> product = orderRepository.findById(id);
        return product.map(orderReadDtoMapper::map);
    }
    @Transactional
    public Optional<OrderReadDto> update(Integer id, OrderReadDto orderReadDto) {
        Optional<Order> foundOrder = orderRepository.findById(id);
        if (foundOrder.isPresent()) {
            Order order = foundOrder.get();
            order.setId(id);
            order.setProductId(orderReadDto.getProductId());
            order.setUserId(orderReadDto.getUserId());
            order.setQuantity(orderReadDto.getQuantity());
            order.setTotalPrice(orderReadDto.getTotalPrice());
            order.setStatus(orderReadDto.getStatus());
            Order product = orderRepository.save(order);
            return Optional.of(orderReadDtoMapper.map(product));
        }
        return Optional.empty();
    }

    @Transactional
    public boolean delete(Integer id) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            orderRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
