package dev.reylibutan.bestore.product;

import dev.reylibutan.bestore.common.domain.Amount;
import dev.reylibutan.bestore.common.domain.Currency;
import dev.reylibutan.bestore.common.domain.GenericMapper;
import dev.reylibutan.bestore.common.exception.ResourceNotFoundException;
import dev.reylibutan.bestore.productdiscount.ProductDiscount;
import dev.reylibutan.bestore.productdiscount.ProductDiscountDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static dev.reylibutan.bestore.common.domain.Currency.HKD;
import static dev.reylibutan.bestore.common.domain.Currency.USD;
import static java.util.Collections.emptyList;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  private ProductService prodSvc;
  private static GenericMapper mapper;
  @Mock
  private ProductRepository prodRepo;

  @BeforeAll
  static void init() {
    mapper = Mappers.getMapper(GenericMapper.class);
  }

  @BeforeEach
  void setUp() {
    prodSvc = new ProductServiceImpl(prodRepo, mapper);
  }

  @Test
  void findAll_shouldReturnEmpty_whenRepoIsEmpty() {
    // given
    when(prodRepo.findAll()).thenReturn(emptyList());

    // when
    List<ProductDto> actualProds = prodSvc.findAll();

    // then
    assertThatCollection(actualProds).isEmpty();
  }

  @Test
  void findAll_shouldReturnAllProducts() {
    // given
    List<Product> expectedProds = getProducts();
    when(prodRepo.findAll()).thenReturn(expectedProds);

    // when
    List<ProductDto> actualProds = prodSvc.findAll();

    // then
    assertThatCollection(actualProds).hasSize(expectedProds.size());
  }

  @Test
  void findAll_shouldNotMapPriceDiscountByDefault() {
    // given
    List<Product> expectedProds = getProducts();
    when(prodRepo.findAll()).thenReturn(expectedProds);

    // when
    List<ProductDto> actualProds = prodSvc.findAll();

    // then
    assertThat(actualProds.get(3)).isNotNull();
    assertThat(actualProds.get(3).getDiscount()).isNull();
  }

  @Test
  void findById_shouldThrowNotFoundException_whenProductDoesNotExist() {
    // given
    when(prodRepo.findById(anyLong())).thenReturn(Optional.empty());

    // then
    assertThatExceptionOfType(ResourceNotFoundException.class).isThrownBy(() -> {
      prodSvc.findById(999L);
    }).withMessage("Product not found. { id: 999 }");
  }

  @Test
  void findById_shouldReturnProduct_whenProductExists() {
    // given
    int prodIdToFind = 2;
    Product expectedProd = getProducts().get(prodIdToFind);
    when(prodRepo.findById((long) prodIdToFind)).thenReturn(of(expectedProd));

    // when
    ProductDto actualProd = prodSvc.findById((long) prodIdToFind);

    // then
    assertThat(actualProd).isNotNull();
    assertThat(actualProd.getId()).isEqualTo(expectedProd.getId());
    assertThat(actualProd.getName()).isEqualTo(expectedProd.getName());
    assertThat(actualProd.getCurrency()).isEqualTo(expectedProd.getCurrency());
    assertThat(actualProd.getPrice()).isEqualTo(new Amount(expectedProd.getPrice()));
  }

  @Test
  void findById_shouldNotMapPriceDiscountByDefault() {
    // given
    int prodIdToFind = 3;
    Product expectedProd = getProducts().get(prodIdToFind);
    when(prodRepo.findById((long) prodIdToFind)).thenReturn(of(expectedProd));

    // when
    ProductDto actualProd = prodSvc.findById((long) prodIdToFind);

    // then
    assertThat(actualProd).isNotNull();
    assertThat(actualProd.getDiscount()).isNull();
  }

  @Test
  void findProductAndDiscountById_shouldThrowNotFoundException_whenProductDoesNotExist() {
    // given
    when(prodRepo.findProductAndDiscountById(anyLong())).thenReturn(Optional.empty());

    // then
    assertThatExceptionOfType(ResourceNotFoundException.class).isThrownBy(() -> {
      prodSvc.findProductAndDiscountById(999L);
    }).withMessage("Product not found. { id: 999 }");
  }

  @Test
  void findProductAndDiscountById_shouldNotMapPriceDiscountByDefault() {
    // given
    int prodIdToFind = 3;
    Product expectedProd = getProducts().get(prodIdToFind);
    when(prodRepo.findProductAndDiscountById((long) prodIdToFind)).thenReturn(of(expectedProd));

    // when
    ProductDto actualProd = prodSvc.findProductAndDiscountById((long) prodIdToFind);

    // then
    assertThat(actualProd).isNotNull();

    ProductDiscountDto actualProdDiscount = actualProd.getDiscount();
    assertThat(actualProdDiscount).isNotNull();
    assertThat(actualProdDiscount.getProductId()).isEqualTo(expectedProd.getDiscount().getProductId());
    assertThat(actualProdDiscount.getFixedDiscount()).isNull();
    assertThat(actualProdDiscount.getPercentageDiscount()).isEqualTo(new BigDecimal("0.15"));
  }

  @Test
  void findById_And_findProductAndDiscountById_shouldReturnTheSameRecordIfNoDiscount() {
    // given
    int prodIdToFind = 1;
    Product expectedProd = getProducts().get(prodIdToFind);
    when(prodRepo.findById((long) prodIdToFind)).thenReturn(of(expectedProd));
    when(prodRepo.findProductAndDiscountById((long) prodIdToFind)).thenReturn(of(expectedProd));

    // when
    ProductDto actualProd1 = prodSvc.findById((long) prodIdToFind);
    ProductDto actualProd2 = prodSvc.findProductAndDiscountById((long) prodIdToFind);

    // then
    assertThat(actualProd1).isEqualTo(actualProd2);
  }

  @Test
  void save_shouldSucceed_whenRecordDoesNotYetExist() {
    // given
    String name = "Product 5";
    String priceStr = "100.55";
    ProductDto prodInput = new ProductDto(777L, name, HKD, new Amount(priceStr));
    prodInput.setCurrency(USD);
    Product prod = new Product(5L, name, USD, new BigDecimal(priceStr));
    when(prodRepo.save(any())).thenReturn(prod);

    // when
    ProductDto actualProd = prodSvc.save(prodInput);

    // then
    assertThat(actualProd).isEqualTo(prodInput);
    assertThat(actualProd.getId()).isEqualTo(prod.getId());
  }

  @Test
  void save_shouldAddDefaultCurrency_whenCurrencyIsNull() {
    // given
    String name = "Product 5";
    String priceStr = "100.55";
    ProductDto prodInput = new ProductDto(777L, name, HKD, new Amount(priceStr));
    Product prod = new Product(5L, name, Currency.getDefault(), new BigDecimal(priceStr));
    when(prodRepo.save(any())).thenReturn(prod);

    // when
    ProductDto actualProd = prodSvc.save(prodInput);

    // then
    assertThat(actualProd.getCurrency()).isEqualTo(Currency.getDefault());
  }

  @Test
  void deleteById_shouldNotComplain_whenProductDoesNotExist() {// when
    prodSvc.deleteById(999L);

    // then
    verify(prodRepo, times(1)).removeById(anyLong());
  }

  @Test
  void deleteById_shouldNotComplain_whenProductExists() {// when
    prodSvc.deleteById(1L);

    // then
    verify(prodRepo, times(1)).removeById(anyLong());
  }

  private List<Product> getProducts() {
    return List.of(
        new Product(1L, "Product 1", HKD, new BigDecimal("100.11")),
        new Product(2L, "Product 2", HKD, new BigDecimal("100.22")),
        new Product(3L, "Product 3", HKD, new BigDecimal("100.33")),
        new Product(4L, "Product 4", HKD, new BigDecimal("100.44"),
            new ProductDiscount(1L, null, new BigDecimal("0.15"))));
  }
}