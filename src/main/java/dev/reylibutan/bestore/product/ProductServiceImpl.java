package dev.reylibutan.bestore.product;

import dev.reylibutan.bestore.common.domain.Currency;
import dev.reylibutan.bestore.common.domain.GenericMapper;
import dev.reylibutan.bestore.common.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

  private final GenericMapper mapper;
  private final ProductRepository productRepo;

  public ProductServiceImpl(ProductRepository productRepo, GenericMapper mapper) {
    this.productRepo = productRepo;
    this.mapper = mapper;
  }

  @Override
  public List<ProductDto> findAll() {
    return mapper.convertToDtoList(productRepo.findAll());
  }

  @Override
  public ProductDto findById(Long id) throws ResourceNotFoundException {
    Optional<Product> product = productRepo.findById(id);
    return product.map(mapper::convertToDtoWithoutLazyLoadingDiscounts).orElseThrow(() -> new ResourceNotFoundException("Product not found. { id: " + id + " }"));
  }

  @Override
  public ProductDto findProductAndDiscountById(Long id) throws ResourceNotFoundException {
    Optional<Product> product = productRepo.findProductAndDiscountById(id);
    return product.map(mapper::convertToDto).orElseThrow(() -> new ResourceNotFoundException("Product not found. { id: " + id + " }"));
  }

  @Override
  @Transactional
  public ProductDto save(ProductDto productDto) {
    Product product = mapper.convertToEntity(productDto);
    // we still allow null currencies from request, so we decide a default for them
    if (product.getCurrency() == null) {
      Currency defaultCurency = Currency.getDefault();
      product.setCurrency(defaultCurency);
      productDto.setCurrency(defaultCurency);
    }

    product = productRepo.save(product);
    // we get and set the recently generated ID (sequence)
    productDto.setId(product.getId());

    return productDto;
  }

  @Override
  @Transactional
  public void deleteById(Long id) {
    Integer deletedRows = productRepo.removeById(id);
    if (deletedRows > 0) {
      log.info("Product deleted successfully. { id: " + id + " }");
    } else {
      log.info("No Product matched the provided ID. Nothing was deleted. { id: " + id + " }");
    }
  }
}
