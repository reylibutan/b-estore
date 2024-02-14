package dev.reylibutan.bestore.productdiscount;

import dev.reylibutan.bestore.common.domain.Amount;
import dev.reylibutan.bestore.common.domain.GenericMapper;
import dev.reylibutan.bestore.common.exception.ValidationFailureException;
import dev.reylibutan.bestore.product.ProductDto;
import dev.reylibutan.bestore.product.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ProductDiscountServiceImpl implements ProductDiscountService {

  private final GenericMapper mapper;
  private final ProductService productService;
  private final ProductDiscountRepository productDiscountRepo;

  public ProductDiscountServiceImpl(GenericMapper mapper, ProductService productService, ProductDiscountRepository productDiscountRepo) {
    this.mapper = mapper;
    this.productService = productService;
    this.productDiscountRepo = productDiscountRepo;
  }

  @Override
  @Transactional
  public void addDiscountToProduct(ProductDiscountDto productDiscountDto) {
    ProductDto productDto = productService.findProductAndDiscountById(productDiscountDto.getProductId());
    validate(productDto, productDiscountDto);

    ProductDiscount productDiscount = mapper.convertToEntity(productDiscountDto);
    productDiscountRepo.save(productDiscount);
  }

  private void validate(ProductDto productDto, ProductDiscountDto productDiscountDto) {
    Long productId = productDiscountDto.getProductId();
    if (productDto == null) {
      throw new ValidationFailureException("Product not found - unable to add discount. { productId: " + productId + " }");
    }

    Amount fixedDiscount = productDiscountDto.getFixedDiscount();
    if (fixedDiscount != null && fixedDiscount.getPrice().compareTo(productDto.getPrice().getPrice()) > 0) {
      throw new ValidationFailureException(
          String.format("Fixed discount cannot be larger than the actual product price. { fixedDiscount: %s, actualPrice: %s }", fixedDiscount, productDto.getPrice()));
    }

    BigDecimal percentageDiscount = productDiscountDto.getPercentageDiscount();
    if (percentageDiscount != null && percentageDiscount.compareTo(BigDecimal.ONE) >= 0) {
      throw new ValidationFailureException(
          String.format("Percentage discount cannot be equal or larger than 100%%. { percentageDiscount: %s }", percentageDiscount));
    } else if (percentageDiscount != null && percentageDiscount.compareTo(BigDecimal.ZERO) < 0) {
      throw new ValidationFailureException(
          String.format("Percentage discount cannot be negative. { percentageDiscount: %s }", percentageDiscount));
    }
  }
}
