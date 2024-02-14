package dev.reylibutan.bestore.common.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.math.RoundingMode.HALF_UP;

@Data
public class Amount {

  private static final int DEFAULT_SCALE = 4;
  private static final RoundingMode DEFAULT_ROUNDING_MODE = HALF_UP;

  @JsonUnwrapped
  private final BigDecimal price;

  public Amount(BigDecimal value) {
    value = value.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    this.price = value;
  }

  public Amount(String value) {
    BigDecimal num = new BigDecimal(value);
    num = num.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    this.price = num;
  }

  public Amount(Integer value) {
    this(value.toString());
  }

  public Amount(Long value) {
    this(value.toString());
  }
}
