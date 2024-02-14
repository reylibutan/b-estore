package dev.reylibutan.bestore.cart;

import dev.reylibutan.bestore.common.validation.NotNullOrZero;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartItemUpdateDto {

  @NotNull(message = "productId {validation.common.not_null}")
  private Long productId;

  @NotNull
  @NotNullOrZero(message = "quantityDelta {validation.common.not_null_or_zero}")
  private Integer quantityDelta;

  public CartItemUpdateDto(Long productId, Integer quantityDelta) {
    this.productId = productId;
    this.quantityDelta = quantityDelta;
  }
}
