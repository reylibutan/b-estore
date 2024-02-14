package dev.reylibutan.bestore.cart;

import dev.reylibutan.bestore.product.Product;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static jakarta.persistence.FetchType.LAZY;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "CART_ITEMS")
public class CartItem {

  @EmbeddedId
  private CartItemId id;

  @ToString.Exclude
  @ManyToOne(fetch = LAZY)
  @MapsId("cartId")
  private Cart cart;

  @ManyToOne(fetch = LAZY)
  @MapsId("productId")
  private Product product;

  @Column(nullable = false)
  private Integer quantity;

  public CartItem(CartItemId id, Integer quantity) {
    this.id = id;
    this.quantity = quantity;
  }
}
