package dev.reylibutan.bestore.cart;

import dev.reylibutan.bestore.common.domain.Amount;
import dev.reylibutan.bestore.common.domain.GenericMapper;
import dev.reylibutan.bestore.common.exception.ResourceNotFoundException;
import dev.reylibutan.bestore.common.exception.ValidationFailureException;
import dev.reylibutan.bestore.product.Product;
import dev.reylibutan.bestore.product.ProductDto;
import dev.reylibutan.bestore.product.ProductRepository;
import dev.reylibutan.bestore.productdiscount.ProductDiscountDto;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Service
public class CartServiceImpl implements CartService {

  private final EntityManager em;
  private final GenericMapper mapper;
  private final CartRepository cartRepo;
  private final ProductRepository productRepo;

  public CartServiceImpl(EntityManager em, GenericMapper mapper, CartRepository cartRepo, ProductRepository productRepo) {
    this.em = em;
    this.mapper = mapper;
    this.cartRepo = cartRepo;
    this.productRepo = productRepo;
  }

  @Override
  public CartDto findById(Long id) throws ResourceNotFoundException {
    CartDto cartDto = cartRepo.findIncludingProductDiscountsById(id)
        .map(mapper::convertToDto)
        .orElseThrow(() -> new ResourceNotFoundException("Cart not found. { id: " + id + " }"));

    // cart summary
    updateCartItemsWithPrices(cartDto);

    return cartDto;
  }

  @Override
  @Transactional
  public CartDto updateCartItems(Long cartId, List<CartItemUpdateDto> cartItems) throws ResourceNotFoundException {
    Cart cart = validateThatCartExists(cartId);

    Set<Long> productIds = extractProductIds(cartItems);
    validateAllProductIdsExist(cartItems);

    // try to get existing items in the cart, so we know if we need to increment/decrement
    // or simply use the quantity as initial quantity
    List<CartItemId> itemIds = buildCartItemIds(cartId, productIds);
    List<CartItem> existingItems = cart.getItems();
    for (CartItemId itemId : itemIds) {
      int matchingCartItemIndex = tryToGetIndexOfExistingCartItem(itemId, existingItems);
      Integer quantityDelta = getQuantityDelta(itemId.getProductId(), cartItems);
      if (matchingCartItemIndex >= 0) {
        // product already exists so we just update the quantity
        CartItem itemToUpdate = existingItems.get(matchingCartItemIndex);
        updateCartItemQuantity(itemToUpdate, quantityDelta);

        if (itemToUpdate.getQuantity().compareTo(0) <= 0) {
          // if new quantity is now less than or equal to zero, we remove it instead
          existingItems.remove(itemToUpdate);
        }
      } else if (quantityDelta.compareTo(0) <= 0) {
        throw new ValidationFailureException("Initial cart item quantity cannot be negative or zero.");
      } else {
        // newly-added product - we create a new cart item
        CartItem newCartItem = new CartItem(itemId, quantityDelta);
        newCartItem.setCart(cart);
        newCartItem.setProduct(em.getReference(Product.class, itemId.getProductId()));
        existingItems.add(newCartItem);
      }
    }

    cartRepo.saveAndFlush(cart);

    return mapper.convertToDto(cart);
  }

  private void updateCartItemsWithPrices(CartDto cartDto) {
    List<CartItemDto> items = cartDto.getItems();
    if (isEmpty(items)) {
      return;
    }

    BigDecimal totalCartPrice = BigDecimal.ZERO;
    for (CartItemDto item : items) {
      BigDecimal totalPrice = computeCartItemTotalPrice(item);
      item.setTotalPrice(new Amount(totalPrice));
      item.setTotalPriceCurrency(item.getProduct().getCurrency());
      cartDto.setTotalPriceCurrency(item.getProduct().getCurrency());

      totalCartPrice = totalCartPrice.add(totalPrice);
    }

    cartDto.setTotalPrice(new Amount(totalCartPrice));
  }

  private BigDecimal computeCartItemTotalPrice(CartItemDto item) {
    ProductDto product = item.getProduct();
    ProductDiscountDto discount = product.getDiscount();
    BigDecimal price = product.getPrice().getPrice();

    BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
    BigDecimal totalPrice = price.multiply(quantity);

    // if product is discounted
    if (discount != null) {
      BigDecimal percentageDiscount = discount.getPercentageDiscount();
      if (percentageDiscount != null) {
        BigDecimal factor = BigDecimal.ONE.subtract(percentageDiscount);
        totalPrice = totalPrice.multiply(factor);
      } else {
        BigDecimal fixedDiscount = discount.getFixedDiscount().getPrice();
        totalPrice = totalPrice.subtract(fixedDiscount.multiply(quantity));
      }
    }

    return totalPrice;
  }

  private Cart validateThatCartExists(Long cartId) {
    return cartRepo.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart not found. { id: " + cartId + " }"));
  }

  private void validateAllProductIdsExist(List<CartItemUpdateDto> cartItems) {
    if (isEmpty(cartItems)) {
      throw new ValidationFailureException("Cart items cannot be empty.");
    }

    Set<Long> productIds = cartItems.stream().map(CartItemUpdateDto::getProductId).collect(Collectors.toSet());
    Integer count = productRepo.countAllByIdIn(productIds);
    if (count != productIds.size()) {
      throw new ValidationFailureException("Some productIds provided don't exist.");
    }
  }

  private Set<Long> extractProductIds(List<CartItemUpdateDto> cartItems) {
    return cartItems.stream().map(CartItemUpdateDto::getProductId).collect(Collectors.toSet());
  }

  private List<CartItemId> buildCartItemIds(Long cartId, Set<Long> productIds) {
    return productIds.stream().map(pi -> new CartItemId(cartId, pi)).toList();
  }

  private int tryToGetIndexOfExistingCartItem(CartItemId id, List<CartItem> existingCartItems) {
    CartItem dummyItemForComparison = new CartItem();
    dummyItemForComparison.setId(id);

    return existingCartItems.indexOf(dummyItemForComparison);
  }

  private Integer getQuantityDelta(Long productId, List<CartItemUpdateDto> cartItems) {
    // we reverse here because we are interested with the last occurrence in cases where request contains duplicate productIds
    return cartItems.reversed().stream()
        .filter(i -> i.getProductId().equals(productId))
        .findFirst()
        .map(CartItemUpdateDto::getQuantityDelta)
        .orElse(0);
  }

  private void updateCartItemQuantity(CartItem item, Integer quantityDelta) {
    item.setQuantity(item.getQuantity() + quantityDelta);
  }
}
