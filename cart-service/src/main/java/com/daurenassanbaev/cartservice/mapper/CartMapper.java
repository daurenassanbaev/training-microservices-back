package com.daurenassanbaev.cartservice.mapper;

import com.daurenassanbaev.cartservice.database.entity.Cart;
import com.daurenassanbaev.cartservice.dto.CartDto;
import org.springframework.stereotype.Component;

@Component
public class CartMapper implements Mapper<CartDto, Cart> {
    @Override
    public Cart map(CartDto object) {
        Cart cart = new Cart();
        copy(object, cart);
        return cart;
    }

    public void copy(CartDto cartDto, Cart cart) {
        cart.setUserId(cartDto.getUserId());
        cart.setId(cartDto.getId());
    }
}
