package dev.reylibutan.bestore.productdiscount;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.reylibutan.bestore.common.domain.Amount;
import dev.reylibutan.bestore.common.validation.EitherDiscountTypeOnly;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@EitherDiscountTypeOnly
public class ProductDiscountDto {

  @NotNull(message = "productId {validation.common.not_null}")
  private Long productId;

  @JsonIgnore
  private int applyDiscountEveryNthItem = 1;

  private Amount fixedDiscount;

  @Max(value = 1, message = "percentageDiscount {jakarta.validation.constraints.Max.message}")
  private BigDecimal percentageDiscount;

  public ProductDiscountDto(Long productId, Amount fixedDiscount, BigDecimal percentageDiscount) {
    this.productId = productId;
    this.fixedDiscount = fixedDiscount;
    this.percentageDiscount = percentageDiscount;
  }
}
