package dev.reylibutan.bestore.cart;

import dev.reylibutan.bestore.common.api.BaseApiController;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static dev.reylibutan.bestore.common.api.BaseApiController.BASE_API_PATH;

@RestController
@RequestMapping(value = BASE_API_PATH + "/cart")
public class CartApiController extends BaseApiController {

  // Assumption - this is supposed to be retrieved from session but since we don't have auth, we hardcode for now
  private static final Long DEFAULT_CART_ID = 1L;

  private final CartService cartService;

  public CartApiController(CartService cartService) {
    this.cartService = cartService;
  }

  @GetMapping
  private CartDto get() {
    return cartService.findById(DEFAULT_CART_ID);
  }

  @PostMapping
  private CartDto updateCartItems(@Valid @RequestBody List<CartItemUpdateDto> cartItemsDto) {
    return cartService.updateCartItems(DEFAULT_CART_ID, cartItemsDto);
  }
}
