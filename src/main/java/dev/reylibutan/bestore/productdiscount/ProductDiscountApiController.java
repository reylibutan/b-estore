package dev.reylibutan.bestore.productdiscount;

import dev.reylibutan.bestore.common.api.BaseApiController;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static dev.reylibutan.bestore.common.api.BaseApiController.BASE_API_PATH_ADMIN;

@RestController
@RequestMapping(value = BASE_API_PATH_ADMIN + "/product-discounts")
public class ProductDiscountApiController extends BaseApiController {

  private final ProductDiscountService productDiscountService;

  public ProductDiscountApiController(ProductDiscountService productDiscountService) {
    this.productDiscountService = productDiscountService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  private ProductDiscountDto addDiscountToProduct(@Valid @RequestBody ProductDiscountDto productDiscountDto) {
    productDiscountService.addDiscountToProduct(productDiscountDto);

    return productDiscountDto;
  }
}
