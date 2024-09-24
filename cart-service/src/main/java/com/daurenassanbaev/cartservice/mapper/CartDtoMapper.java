package com.daurenassanbaev.cartservice.mapper;

import com.daurenassanbaev.cartservice.database.entity.Cart;
import com.daurenassanbaev.cartservice.dto.CartDto;
import org.springframework.stereotype.Component;

@Component
public class CartDtoMapper implements Mapper<Cart, CartDto> {


    @Override
    public CartDto map(Cart object) {
        CartDto cartDto = new CartDto();
        copy(object, cartDto);
        return cartDto;
    }
    public void copy(Cart cart, CartDto cartDto) {
        cartDto.setUserId(cart.getUserId());
        cartDto.setId(cart.getId());
    }
}
