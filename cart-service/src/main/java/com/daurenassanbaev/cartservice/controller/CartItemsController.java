package com.daurenassanbaev.cartservice.controller;

import com.daurenassanbaev.cartservice.dto.CartItemDto;
import com.daurenassanbaev.cartservice.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/cart-items")
public class CartItemsController {
    private final CartService cartService;

    @GetMapping("/all/{id}")
    public ResponseEntity<List<CartItemDto>> findAll(@PathVariable("id") Integer id) {
        List<CartItemDto> list = cartService.findAllItems(id);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @GetMapping("/{id}")
    public CartItemDto findById(@PathVariable("id") Integer id) {
        return cartService.findByItemId(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<HttpStatus> create(@RequestBody CartItemDto cartDto) {
        cartService.create(cartDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CartItemDto update(@PathVariable("id") Integer id, @RequestBody CartItemDto cartItemDto) {
        return cartService.updateItem(id, cartItemDto).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) {
        if (!cartService.delete(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
