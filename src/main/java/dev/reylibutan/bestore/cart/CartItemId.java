package dev.reylibutan.bestore.cart;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Embeddable
public class CartItemId implements Serializable {

  @Column(name = "CART_ID")
  private Long cartId;

  @Column(name = "PRODUCT_ID")
  private Long productId;

  public CartItemId(Long cartId, Long productId) {
    this.cartId = cartId;
    this.productId = productId;
  }
}
