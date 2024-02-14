package dev.reylibutan.bestore.cart;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.EAGER;

@Data
@NoArgsConstructor
@Entity
@Table(name = "CARTS")
public class Cart {

  @Id
  private Long id;

  @OneToMany(mappedBy = "cart", cascade = ALL, fetch = EAGER)
  private List<CartItem> items = new ArrayList<>();

  public Cart(Long id, List<CartItem> items) {
    this.id = id;
    this.items = items;
  }
}
