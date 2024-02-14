package dev.reylibutan.bestore.cart;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

  @EntityGraph(attributePaths = {"items.product.discount"})
  Optional<Cart> findIncludingProductDiscountsById(Long id);
}
