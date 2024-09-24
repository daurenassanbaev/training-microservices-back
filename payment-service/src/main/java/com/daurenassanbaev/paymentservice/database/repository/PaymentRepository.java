package com.daurenassanbaev.paymentservice.database.repository;

import com.daurenassanbaev.paymentservice.database.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
}
