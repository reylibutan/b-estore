package dev.reylibutan.bestore.cart;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import dev.reylibutan.bestore.common.domain.Amount;
import dev.reylibutan.bestore.common.domain.Currency;
import dev.reylibutan.bestore.product.ProductDto;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartItemDto {

  private ProductDto product;
  private Integer quantity;

  private Currency totalPriceCurrency;
  @JsonUnwrapped
  private Amount totalPrice;

  public CartItemDto(ProductDto product, Integer quantity) {
    this.product = product;
    this.quantity = quantity;
  }
}
