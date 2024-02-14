package dev.reylibutan.bestore.productdiscount;

import dev.reylibutan.bestore.product.Product;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Entity
@Table(name = "PRODUCT_DISCOUNTS")
public class ProductDiscount {

  @Id
  private Long productId;

  // TODO - still unsupported, this will allow us to apply discounts like (50% off on 2nd item)
  @Column(name = "APPLY_EVERY_NTH", nullable = false, columnDefinition = "INT DEFAULT 1")
  private Integer applyDiscountEveryNthItem = 1;

  @Column(precision = 20, scale = 4)
  private BigDecimal fixedDiscount;

  @Column(precision = 5, scale = 4)
  private BigDecimal percentageDiscount;

  @ToString.Exclude
  @OneToOne
  @JoinColumn(name = "PRODUCT_ID", referencedColumnName = "ID")
  private Product product;

  public ProductDiscount(Long productId, BigDecimal fixedDiscount, BigDecimal percentageDiscount) {
    this.productId = productId;
    this.fixedDiscount = fixedDiscount;
    this.percentageDiscount = percentageDiscount;
  }
}
