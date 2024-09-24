package com.daurenassanbaev.userservice.mapper;

import com.daurenassanbaev.userservice.database.entity.User;
import com.daurenassanbaev.userservice.dto.UserReadDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserReadDtoMapper implements Mapper<User, UserReadDto> {

    @Override
    public UserReadDto map(User object) {
        UserReadDto userReadDto = new UserReadDto();
        copy(object, userReadDto);
        return userReadDto;
    }
    private void copy(User object, UserReadDto userReadDto) {
        userReadDto.setId(object.getId());
        userReadDto.setBalance(object.getBalance());
    }

}