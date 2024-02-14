package dev.reylibutan.bestore.productdiscount;

import dev.reylibutan.bestore.common.domain.Amount;
import dev.reylibutan.bestore.common.domain.GenericMapper;
import dev.reylibutan.bestore.common.exception.ValidationFailureException;
import dev.reylibutan.bestore.product.ProductDto;
import dev.reylibutan.bestore.product.ProductService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static dev.reylibutan.bestore.common.domain.Currency.HKD;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductDiscountServiceTest {

  private ProductDiscountService prodDiscountSvc;
  private static GenericMapper mapper;
  @Mock
  private ProductService prodSvc;
  @Mock
  private ProductDiscountRepository prodDiscountRepo;

  @BeforeAll
  static void init() {
    mapper = Mappers.getMapper(GenericMapper.class);
  }

  @BeforeEach
  void setUp() {
    prodDiscountSvc = new ProductDiscountServiceImpl(mapper, prodSvc, prodDiscountRepo);
  }

  @Test
  void shouldThrowValidationFailureException_whenProductIdDoesNotExist() {
    // given
    Long nonExistentProdId = 999L;
    when(prodSvc.findProductAndDiscountById(nonExistentProdId)).thenReturn(null);

    // then
    assertThatExceptionOfType(ValidationFailureException.class).isThrownBy(() -> {
      ProductDiscountDto prodDiscountDto = getDiscount();
      prodDiscountDto.setProductId(nonExistentProdId);
      prodDiscountSvc.addDiscountToProduct(prodDiscountDto);
    }).withMessageContaining("Product not found - unable to add discount.");
  }

  @Test
  void shouldThrowValidationFailureException_whenFixedDiscountIsLargerThanProductPrice() {
    // given
    ProductDto prod = getProductWithPrice(new Amount("100.11"));
    ProductDiscountDto prodDiscount = getDiscountWithFixedDiscount(new Amount("200"));
    prod.setDiscount(prodDiscount);

    when(prodSvc.findProductAndDiscountById(1L)).thenReturn(prod);

    // then
    assertThatExceptionOfType(ValidationFailureException.class).isThrownBy(() -> {
      prodDiscountSvc.addDiscountToProduct(prodDiscount);
    }).withMessageContaining("Fixed discount cannot be larger than the actual product price.");
  }

  @Test
  void shouldThrowValidationFailureException_whenPercentageDiscountIsLargerThan100Percent() {
    // given
    ProductDto prod = getProduct();
    ProductDiscountDto prodDiscount = getDiscountWithPercentageDiscount(BigDecimal.ONE);
    prod.setDiscount(prodDiscount);

    when(prodSvc.findProductAndDiscountById(1L)).thenReturn(prod);

    // then
    assertThatExceptionOfType(ValidationFailureException.class).isThrownBy(() -> {
      prodDiscountSvc.addDiscountToProduct(prodDiscount);
    }).withMessageContaining("Percentage discount cannot be equal or larger than 100%.");
  }

  @Test
  void shouldThrowValidationFailureException_whenPercentageDiscountIsNegative() {
    // given
    ProductDto prod = getProduct();
    ProductDiscountDto prodDiscount = getDiscountWithPercentageDiscount(new BigDecimal("-1"));
    prod.setDiscount(prodDiscount);

    when(prodSvc.findProductAndDiscountById(1L)).thenReturn(prod);

    // then
    assertThatExceptionOfType(ValidationFailureException.class).isThrownBy(() -> {
      prodDiscountSvc.addDiscountToProduct(prodDiscount);
    }).withMessageContaining("Percentage discount cannot be negative.");
  }

  @Test
  void shouldSuccessFullyAdd() {
    // given
    ProductDto prod = getProduct();
    ProductDiscountDto prodDiscount = getDiscount();
    prod.setDiscount(prodDiscount);

    when(prodSvc.findProductAndDiscountById(1L)).thenReturn(prod);

    // when
    prodDiscountSvc.addDiscountToProduct(prodDiscount);
  }

  private ProductDto getProduct() {
    return new ProductDto(1L, "Product 1", HKD, new Amount("100"));
  }

  private ProductDto getProductWithPrice(Amount price) {
    return new ProductDto(1L, "Product 1", HKD, price);
  }

  private ProductDiscountDto getDiscount() {
    return new ProductDiscountDto(1L, null, new BigDecimal("0.15"));
  }

  private ProductDiscountDto getDiscountWithFixedDiscount(Amount fixedDiscount) {
    return new ProductDiscountDto(1L, fixedDiscount, null);
  }

  private ProductDiscountDto getDiscountWithPercentageDiscount(BigDecimal percentageDiscount) {
    return new ProductDiscountDto(1L, null, percentageDiscount);
  }
}