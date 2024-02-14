package dev.reylibutan.bestore.cart;

import dev.reylibutan.bestore.common.domain.Amount;
import dev.reylibutan.bestore.common.domain.GenericMapper;
import dev.reylibutan.bestore.common.exception.ResourceNotFoundException;
import dev.reylibutan.bestore.product.Product;
import dev.reylibutan.bestore.product.ProductRepository;
import dev.reylibutan.bestore.productdiscount.ProductDiscount;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static dev.reylibutan.bestore.common.domain.Currency.HKD;
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

  private CartService cartSvc;
  private static GenericMapper mapper;
  @Mock
  private EntityManager em;
  @Mock
  private CartRepository cartRepo;
  @Mock
  private ProductRepository productRepo;

  @BeforeAll
  static void init() {
    mapper = Mappers.getMapper(GenericMapper.class);
  }

  @BeforeEach
  void setUp() {
    cartSvc = new CartServiceImpl(em, mapper, cartRepo, productRepo);
  }

  @Test
  void findById_shouldThrowResourceNotFoundException_whenCartIdDoesNotExist() {
    // given
    when(cartRepo.findIncludingProductDiscountsById(anyLong())).thenReturn(empty());

    // then
    assertThatExceptionOfType(ResourceNotFoundException.class).isThrownBy(() -> {
      cartSvc.findById(1L);
    }).withMessageContaining("Cart not found.");
  }

  @Test
  void findById_shouldComputeZeroTotalPrice_whenCartIsEmpty() {
    // given
    Cart cart = getEmptyCart();
    when(cartRepo.findIncludingProductDiscountsById(anyLong())).thenReturn(of(cart));

    // when
    CartDto actualCart = cartSvc.findById(1L);

    // then
    assertThat(actualCart).isNotNull();
    assertThat(actualCart.getTotalPrice()).isNull();
  }

  @Test
  void findById_shouldComputeTotalPrice_whenCartIsNotEmptyWithoutDiscount() {
    // given
    Cart cart = getEmptyCart();
    CartItem cartItem1 = createCartItem(new BigDecimal("100"), 10); // 1000
    CartItem cartItem2 = createCartItem(new BigDecimal("200"), 2); // 400
    cart.setItems(List.of(cartItem1, cartItem2));

    when(cartRepo.findIncludingProductDiscountsById(anyLong())).thenReturn(of(cart));

    // when
    CartDto actualCart = cartSvc.findById(1L);

    // then
    assertThat(actualCart).isNotNull();
    assertThat(actualCart.getTotalPrice()).isEqualTo(new Amount(new BigDecimal("1400")));
  }

  @Test
  void shouldComputeCartItemDiscount_whenFixedDiscountTypeIsAvailable() {
    // given
    Cart cart = getEmptyCart();
    CartItem cartItem1 = createCartItem(new BigDecimal("100"), 10); // 1000
    CartItem cartItem2 = createCartItem(new BigDecimal("200"), 2, new BigDecimal("10"), null); // 380
    cart.setItems(List.of(cartItem1, cartItem2));

    when(cartRepo.findIncludingProductDiscountsById(anyLong())).thenReturn(of(cart));

    // when
    CartDto actualCart = cartSvc.findById(1L);

    // then
    assertThat(actualCart).isNotNull();
    assertThat(actualCart.getTotalPrice()).isEqualTo(new Amount(new BigDecimal("1380")));
  }

  @Test
  void shouldComputeCartItemDiscount_whenPercentageDiscountTypeIsAvailable() {
    // given
    Cart cart = getEmptyCart();
    CartItem cartItem1 = createCartItem(new BigDecimal("100"), 10); // 1000
    CartItem cartItem2 = createCartItem(new BigDecimal("200"), 2, null, new BigDecimal(".1")); // 360
    cart.setItems(List.of(cartItem1, cartItem2));

    when(cartRepo.findIncludingProductDiscountsById(anyLong())).thenReturn(of(cart));

    // when
    CartDto actualCart = cartSvc.findById(1L);

    // then
    assertThat(actualCart).isNotNull();
    assertThat(actualCart.getTotalPrice()).isEqualTo(new Amount(new BigDecimal("1360")));
  }

  @Test
  void shouldComputeCartItemDiscount_whenMixedDiscountsAreAvailable() {
    // given
    Cart cart = getEmptyCart();
    CartItem cartItem1 = createCartItem(new BigDecimal("100"), 10); // 1000
    CartItem cartItem2 = createCartItem(new BigDecimal("200"), 2, new BigDecimal("10"), null); // 360
    CartItem cartItem3 = createCartItem(new BigDecimal("200"), 2, null, new BigDecimal(".1")); // 380
    cart.setItems(List.of(cartItem1, cartItem2, cartItem3));

    when(cartRepo.findIncludingProductDiscountsById(anyLong())).thenReturn(of(cart));

    // when
    CartDto actualCart = cartSvc.findById(1L);

    // then
    assertThat(actualCart).isNotNull();
    assertThat(actualCart.getTotalPrice()).isEqualTo(new Amount(new BigDecimal("1740")));
  }

  @Test
  void addToCart_shouldThrowResourceNotFoundException_whenCartDoesNotExist() {
    // given
    when(cartRepo.findById(anyLong())).thenReturn(empty());

    // then
    assertThatExceptionOfType(ResourceNotFoundException.class).isThrownBy(() -> {
      cartSvc.updateCartItems(1L, emptyList());
    }).withMessageContaining("Cart not found.");
  }

  private Cart getEmptyCart() {
    return new Cart(1L, emptyList());
  }

  private CartItem createCartItem(BigDecimal price, Integer quantity) {
    return createCartItem(price, quantity, null, null);
  }

  private CartItem createCartItem(BigDecimal price, Integer quantity, BigDecimal fixedDiscount, BigDecimal percentageDiscount) {
    Cart cart = getEmptyCart();
    Product prod = getProdWithPrice(price);
    if (fixedDiscount != null) {
      prod.setDiscount(getDiscountWithFixedDiscount(fixedDiscount));
    } else if (percentageDiscount != null) {
      prod.setDiscount(getDiscountWithPercentageDiscount(percentageDiscount));
    }

    CartItemId catItemId = new CartItemId(cart.getId(), prod.getId());
    CartItem cartItem = new CartItem(catItemId, quantity);
    cartItem.setProduct(prod);

    return cartItem;
  }

  private Product getProdWithPrice(BigDecimal price) {
    return new Product(1L, "Product 1", HKD, price);
  }

  private ProductDiscount getDiscountWithFixedDiscount(BigDecimal fixedDiscount) {
    return new ProductDiscount(1L, fixedDiscount, null);
  }

  private ProductDiscount getDiscountWithPercentageDiscount(BigDecimal percentageDiscount) {
    return new ProductDiscount(1L, null, percentageDiscount);
  }

  private CartItemUpdateDto getCartItemDto(Long productId, Integer quantity) {
    return new CartItemUpdateDto(productId, quantity);
  }
}