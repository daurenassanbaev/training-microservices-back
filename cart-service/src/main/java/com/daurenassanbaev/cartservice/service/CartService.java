package com.daurenassanbaev.cartservice.service;

import com.daurenassanbaev.cartservice.database.entity.Cart;
import com.daurenassanbaev.cartservice.database.entity.CartItem;
import com.daurenassanbaev.cartservice.database.repository.CartItemsRepository;
import com.daurenassanbaev.cartservice.database.repository.CartRepository;
import com.daurenassanbaev.cartservice.dto.CartDto;
import com.daurenassanbaev.cartservice.dto.CartItemDto;
import com.daurenassanbaev.cartservice.mapper.CartDtoMapper;
import com.daurenassanbaev.cartservice.mapper.CartItemsDtoMapper;
import com.daurenassanbaev.cartservice.mapper.CartItemsMapper;
import com.daurenassanbaev.cartservice.mapper.CartMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Transactional(readOnly = true)
@Service
@Slf4j
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final CartDtoMapper cartDtoMapper;
    private final CartItemsRepository cartItemsRepository;
    private final CartItemsMapper cartItemsMapper;
    private final CartItemsDtoMapper cartItemsDtoMapper;
    private final KafkaTemplate<String, List<Integer>> list1KafkaTemplate;

    @Transactional
    public void create(Object cartDto) {
        if (cartDto instanceof CartDto) {
            cartRepository.save(cartMapper.map((CartDto) cartDto));
        } else {
            cartItemsRepository.save(cartItemsMapper.map((CartItemDto) cartDto));
        }
    }
    @KafkaListener(topics = {"create-cart"})
    @Transactional
    public void createCart(byte[] data) throws IOException {
        var cart = new CartDto();
        String jsonString = new String(data, StandardCharsets.UTF_8);
        cart.setUserId(jsonString);
        create(cart);
    }

    @KafkaListener(topics = {"create-cart-item"})
    @Transactional
    public void createCartItem(byte[] data1) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> data = objectMapper.readValue(data1, new TypeReference<Map<String, String>>() {});
        var cart = new CartItemDto();
        cart.setCartId(Integer.parseInt(data.get("cartId")));
        cart.setOrderId(Integer.parseInt(data.get("orderId")));
        create(cart);
    }

    @KafkaListener(topics = {"find-cart-items"})
    @Transactional
    public void findCartItems(byte[] data1) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> data = objectMapper.readValue(data1, new TypeReference<Map<String, String>>() {});
        var result = findAllItems(Integer.parseInt(data.get("cartId")));
        var res1 = new ArrayList<Integer>();
        for (var item : result) {
            res1.add(item.getOrderId());
        }
        for (var i: result) {
            cartItemsRepository.deleteById(i.getId());
        }
        list1KafkaTemplate.send("cart-items", res1);

    }

    public List<CartDto> findAll() {
        List<Cart> orders = cartRepository.findAll();
        return orders.stream().map(cartDtoMapper::map).toList();
    }
    public List<CartItemDto> findAllItems(Integer cartId) {
        List<CartItem> orders = cartItemsRepository.findByCartId(cartId);
        return orders.stream().map(cartItemsDtoMapper::map).toList();
    }

    @KafkaListener(topics = {"get-cart-id"})
    public void findByUserId(byte[] data1) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> data = objectMapper.readValue(data1, new TypeReference<Map<String, String>>() {});
        Optional<Cart> cart = cartRepository.findByUserId(data.get("userId"));
        Integer some = cart.get().getId();
        list1KafkaTemplate.send("give-cart-id", List.of(some));
    }

    public Optional<CartDto> findById(Integer id) {
        Optional<Cart> product = cartRepository.findById(id);
        return product.map(cartDtoMapper::map);
    }
    public Optional<CartItemDto> findByItemId(Integer id) {
        Optional<CartItem> product = cartItemsRepository.findById(id);
        return product.map(cartItemsDtoMapper::map);
    }
    @Transactional
    public Optional<CartDto> update(Integer id, CartDto cartDto) {
        Optional<Cart> foundCart = cartRepository.findById(id);
        if (foundCart.isPresent()) {
            Cart cart = foundCart.get();
            cart.setId(id);
            cart.setUserId(cartDto.getUserId());
            cart.setCartItems(cartItemsRepository.findByCartId(id));
            cartRepository.save(cart);
            return Optional.of(cartDto);
        }
        return Optional.empty();
    }
    @Transactional
    public Optional<CartItemDto> updateItem(Integer id, CartItemDto cartDto) {
        Optional<CartItem> foundCart = cartItemsRepository.findById(id);
        if (foundCart.isPresent()) {
            CartItem cart = foundCart.get();
            cart.setId(id);
            cart.setOrderId(cartDto.getOrderId());
            cart.setCart(cartRepository.findById(cartDto.getCartId()).get());
            cartItemsRepository.save(cart);
            return Optional.of(cartDto);
        }
        return Optional.empty();
    }

    @Transactional
    public boolean delete(Integer id) {
        Optional<Cart> order = cartRepository.findById(id);
        if (order.isPresent()) {
            cartRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
