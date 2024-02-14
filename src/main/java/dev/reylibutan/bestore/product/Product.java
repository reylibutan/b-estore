package dev.reylibutan.bestore.product;

import dev.reylibutan.bestore.common.domain.Currency;
import dev.reylibutan.bestore.productdiscount.ProductDiscount;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Data
@NoArgsConstructor
@Entity
@Table(name = "PRODUCTS")
public class Product {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(length = 10, nullable = false)
  private Currency currency;

  @Column(precision = 20, scale = 4, nullable = false)
  private BigDecimal price;

  @OneToOne(mappedBy = "product", cascade = REMOVE, fetch = LAZY)
  private ProductDiscount discount;

  public Product(Long id, String name, Currency currency, BigDecimal price) {
    this(id, name, currency, price, null);
  }

  public Product(Long id, String name, Currency currency, BigDecimal price, ProductDiscount discount) {
    this.id = id;
    this.name = name;
    this.currency = currency;
    this.price = price;
    this.discount = discount;
  }
}
