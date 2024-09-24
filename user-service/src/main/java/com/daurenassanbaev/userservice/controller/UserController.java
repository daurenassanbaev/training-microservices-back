package com.daurenassanbaev.userservice.controller;

import com.daurenassanbaev.userservice.database.entity.User;
import com.daurenassanbaev.userservice.dto.CreateUserDto;
import com.daurenassanbaev.userservice.dto.UpdateUserDto;
import com.daurenassanbaev.userservice.dto.UserDto;
import com.daurenassanbaev.userservice.dto.UserReadDto;
import com.daurenassanbaev.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserReadDto>> findAll() {
        List<UserReadDto> list = userService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @PostMapping
    public ResponseEntity<UserReadDto> create(@RequestBody UserDto createUserDto) {
        var res = userService.create(createUserDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping("/{id}")
    public UserReadDto findById(@PathVariable("id") String id) {
        return userService.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }


    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public UpdateUserDto update(@PathVariable("id") String id, @RequestBody UpdateUserDto userDto) {
        return userService.update(id, userDto).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        if (!userService.delete(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

}
