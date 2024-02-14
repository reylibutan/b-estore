package dev.reylibutan.bestore.product;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import dev.reylibutan.bestore.common.domain.Amount;
import dev.reylibutan.bestore.common.domain.Currency;
import dev.reylibutan.bestore.common.validation.ValidCurrency;
import dev.reylibutan.bestore.productdiscount.ProductDiscount;
import dev.reylibutan.bestore.productdiscount.ProductDiscountDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ProductDto {

  private Long id;

  @NotBlank(message = "name {validation.common.not_blank}")
  private String name;

  @ValidCurrency
  private Currency currency;

  @JsonUnwrapped
  @NotNull(message = "price {validation.common.not_null}")
  private Amount price;

  private ProductDiscountDto discount;

  public ProductDto(Long id, String name, Currency currency, Amount price) {
    this(id, name, currency, price, null);
  }

  public ProductDto(Long id, String name, Currency currency, Amount price, ProductDiscountDto discount) {
    this.id = id;
    this.name = name;
    this.currency = currency;
    this.price = price;
    this.discount = discount;
  }
}
