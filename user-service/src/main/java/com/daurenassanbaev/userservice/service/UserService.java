package com.daurenassanbaev.userservice.service;

import com.daurenassanbaev.userservice.database.entity.User;
import com.daurenassanbaev.userservice.database.repository.UserRepository;
import com.daurenassanbaev.userservice.dto.UpdateUserDto;
import com.daurenassanbaev.userservice.dto.UserDto;
import com.daurenassanbaev.userservice.dto.UserReadDto;
import com.daurenassanbaev.userservice.mapper.UserMapper;
import com.daurenassanbaev.userservice.mapper.UserReadDtoMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserReadDtoMapper userReadDtoMapper;
    private final UserMapper userMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    @Transactional
    public UserReadDto create(UserDto userDto) {
        User user = userRepository.save(userMapper.map(userDto));
        UserReadDto userReadDto = userReadDtoMapper.map(user);
        kafkaTemplate.send("create-cart", userReadDto.getId());
        return userReadDto;
    }

    public List<UserReadDto> findAll() {
        List<User> users = userRepository.findAll();
        return users.stream().map(userReadDtoMapper::map).toList();
    }

    public Optional<UserReadDto> findById(String id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(userReadDtoMapper::map);
    }
    @Transactional
    public Optional<UpdateUserDto> update(String id, UpdateUserDto userDto) {
        Optional<User> foundUser = userRepository.findById(id);
        if (foundUser.isPresent()) {
            User user1 = foundUser.get();
            user1.setId(id);
            user1.setBalance(userDto.getBalance());
            userRepository.save(user1);
            return Optional.of(userDto);
        }
        return Optional.empty();
    }
    @KafkaListener(topics = {"reduce-balance-user"})
    @KafkaHandler
    public void getCartId(String id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public boolean delete(String id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
