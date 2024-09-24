package com.daurenassanbaev.orderservice.database.repository;

import com.daurenassanbaev.orderservice.database.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
}
