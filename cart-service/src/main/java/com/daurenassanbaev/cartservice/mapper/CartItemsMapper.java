package com.daurenassanbaev.cartservice.mapper;

import com.daurenassanbaev.cartservice.database.entity.CartItem;
import com.daurenassanbaev.cartservice.database.repository.CartRepository;
import com.daurenassanbaev.cartservice.dto.CartItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CartItemsMapper implements Mapper<CartItemDto, CartItem> {
    private final CartRepository cartRepository;
    @Override
    public CartItem map(CartItemDto object) {
        CartItem cartItem = new CartItem();
        copy(object, cartItem);
        return cartItem;
    }

    public void copy(CartItemDto object, CartItem cartItem) {
        cartItem.setId(object.getId());
        cartItem.setCart(cartRepository.findById(object.getCartId()).get());
        cartItem.setOrderId(object.getOrderId());
    }

}
