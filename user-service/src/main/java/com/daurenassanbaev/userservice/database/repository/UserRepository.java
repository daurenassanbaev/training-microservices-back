package com.daurenassanbaev.userservice.database.repository;

import com.daurenassanbaev.userservice.database.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findById(String id);
}