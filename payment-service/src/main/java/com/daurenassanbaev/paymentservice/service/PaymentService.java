package com.daurenassanbaev.paymentservice.service;

import com.daurenassanbaev.paymentservice.database.entity.Payment;
import com.daurenassanbaev.paymentservice.database.entity.Status;
import com.daurenassanbaev.paymentservice.database.repository.PaymentRepository;
import com.daurenassanbaev.paymentservice.dto.PaymentDto;
import com.daurenassanbaev.paymentservice.mapper.PaymentDtoMapper;
import com.daurenassanbaev.paymentservice.mapper.PaymentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Transactional(readOnly = true)
@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;
    private final PaymentDtoMapper paymentDtoMapper;
    private final KafkaTemplate<String, Map<String, Integer>> kafkaTemplate;
    private final KafkaTemplate<String, Map<String, String>> strkafkaTemplate;
    private final KafkaTemplate<String, List<Integer>> listKafkaTemplate;
    private List<Integer> orderIds = new ArrayList<>();
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Transactional
    public void create(Integer cartId) {
        PaymentDto paymentDto = new PaymentDto();
        try {
            var res = strkafkaTemplate.send("find-cart-items", Map.of("cartId", ""+cartId)).get();
            while (orderIds.isEmpty()) {
                Thread.sleep(500);
            }
            if (!orderIds.isEmpty()) {
                SendResult<String, List<Integer>> res1 = listKafkaTemplate.send("get-orders-amount", orderIds).get();
                while (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    Thread.sleep(500);
                }
                if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
                    paymentDto.setCartId(cartId);
                    paymentDto.setAmount(totalAmount);
                    paymentDto.setStatus(Status.COMPLETED);
                    paymentRepository.save(paymentMapper.map(paymentDto));
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

    }
    @KafkaListener(topics = {"cart-items"})
    @Transactional
    public void findCartItems(List<Integer> data) {
        this.orderIds = data;
    }
    @KafkaListener(topics = {"total-amount"})
    @Transactional
    public void findCartItems(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<PaymentDto> findAll() {
        List<Payment> orders = paymentRepository.findAll();
        return orders.stream().map(paymentDtoMapper::map).toList();
    }

    public Optional<PaymentDto> findById(Integer id) {
        Optional<Payment> product = paymentRepository.findById(id);
        return product.map(paymentDtoMapper::map);
    }

    @Transactional
    public boolean delete(Integer id) {
        Optional<Payment> order = paymentRepository.findById(id);
        if (order.isPresent()) {
            paymentRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
