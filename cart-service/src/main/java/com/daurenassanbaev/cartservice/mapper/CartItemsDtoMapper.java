package com.daurenassanbaev.cartservice.mapper;

import com.daurenassanbaev.cartservice.database.entity.CartItem;
import com.daurenassanbaev.cartservice.database.repository.CartRepository;
import com.daurenassanbaev.cartservice.dto.CartItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CartItemsDtoMapper implements Mapper<CartItem, CartItemDto> {
    private final CartRepository cartRepository;
    @Override
    public CartItemDto map(CartItem object) {
        CartItemDto cartItemDto = new CartItemDto();
        copy(object, cartItemDto);
        return cartItemDto;
    }

    public void copy(CartItem object, CartItemDto cartItem) {
        cartItem.setId(object.getId());
        cartItem.setCartId(object.getCart().getId());
        cartItem.setOrderId(object.getOrderId());
    }

}
