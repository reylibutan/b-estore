package dev.reylibutan.bestore.cart;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import dev.reylibutan.bestore.common.domain.Amount;
import dev.reylibutan.bestore.common.domain.Currency;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CartDto {

  private Long id;
  private List<CartItemDto> items = new ArrayList<>();

  private Currency totalPriceCurrency;
  @JsonUnwrapped
  private Amount totalPrice;
}
