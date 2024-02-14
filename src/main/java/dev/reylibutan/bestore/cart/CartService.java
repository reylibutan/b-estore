package dev.reylibutan.bestore.cart;

import dev.reylibutan.bestore.common.exception.ResourceNotFoundException;

import java.util.List;

public interface CartService {

  CartDto findById(Long id) throws ResourceNotFoundException;

  CartDto updateCartItems(Long cartId, List<CartItemUpdateDto> cartItems) throws ResourceNotFoundException;
}
