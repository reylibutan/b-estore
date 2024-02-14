package dev.reylibutan.bestore.product;

import dev.reylibutan.bestore.common.api.BaseApiController;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static dev.reylibutan.bestore.common.api.BaseApiController.BASE_API_PATH_ADMIN;

@RestController
@RequestMapping(value = BASE_API_PATH_ADMIN + "/products")
public class ProductApiController extends BaseApiController {

  private final ProductService productService;

  public ProductApiController(ProductService productService) {
    this.productService = productService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  private ProductDto create(@Valid @RequestBody ProductDto product) {
    return productService.save(product);
  }

  @GetMapping
  private List<ProductDto> getAll() {
    // not in the requirements but for testing convenience
    // proper implementation (later) should have pagination
    return productService.findAll();
  }

  @GetMapping("/{id}")
  private ProductDto get(@PathVariable Long id) {
    return productService.findById(id);
  }

  @DeleteMapping("/{id}")
  private void delete(@PathVariable Long id) {
    productService.deleteById(id);
  }
}
