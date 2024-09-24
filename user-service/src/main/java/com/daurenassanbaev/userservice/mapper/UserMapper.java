package com.daurenassanbaev.userservice.mapper;

import com.daurenassanbaev.userservice.database.entity.User;
import com.daurenassanbaev.userservice.dto.CreateUserDto;
import com.daurenassanbaev.userservice.dto.UserDto;
import com.daurenassanbaev.userservice.dto.UserReadDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper implements Mapper<UserDto, User> {
    @Override
    public User map(UserDto object) {
        User user = new User();
        copy(object, user);
        return user;
    }
    private void copy(UserDto object, User user) {
        user.setId(object.getId());
        user.setBalance(object.getBalance());
    }
}
