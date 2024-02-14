package dev.reylibutan.bestore.product;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

  Integer removeById(Long id);

  @EntityGraph(attributePaths = {"discount"})
  Optional<Product> findProductAndDiscountById(Long id);

  Integer countAllByIdIn(Set<Long> ids);
}
