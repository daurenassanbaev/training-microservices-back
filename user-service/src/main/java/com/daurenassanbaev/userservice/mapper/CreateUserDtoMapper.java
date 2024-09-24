package com.daurenassanbaev.userservice.mapper;

import com.daurenassanbaev.userservice.database.entity.User;
import com.daurenassanbaev.userservice.dto.CreateUserDto;
import com.daurenassanbaev.userservice.dto.UserReadDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateUserDtoMapper implements Mapper<CreateUserDto, User> {

    @Override
    public User map(CreateUserDto object) {
        User createUserDto = new User();
        copy(object, createUserDto);
        return createUserDto;
    }
    private void copy(CreateUserDto object, User user) {
        
    }

}