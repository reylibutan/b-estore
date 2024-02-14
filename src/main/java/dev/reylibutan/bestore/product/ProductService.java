package dev.reylibutan.bestore.product;

import dev.reylibutan.bestore.common.exception.ResourceNotFoundException;

import java.util.List;

public interface ProductService {

  List<ProductDto> findAll();

  ProductDto findById(Long id) throws ResourceNotFoundException;

  ProductDto findProductAndDiscountById(Long id) throws ResourceNotFoundException;

  ProductDto save(ProductDto productDto);

  void deleteById(Long id);
}
