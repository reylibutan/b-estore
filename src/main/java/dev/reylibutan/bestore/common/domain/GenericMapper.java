package dev.reylibutan.bestore.common.domain;

import dev.reylibutan.bestore.cart.Cart;
import dev.reylibutan.bestore.cart.CartDto;
import dev.reylibutan.bestore.cart.CartItem;
import dev.reylibutan.bestore.cart.CartItemDto;
import dev.reylibutan.bestore.product.Product;
import dev.reylibutan.bestore.product.ProductDto;
import dev.reylibutan.bestore.productdiscount.ProductDiscount;
import dev.reylibutan.bestore.productdiscount.ProductDiscountDto;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.List;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface GenericMapper {

  Product convertToEntity(ProductDto dto);

  @Named("convertToDto")
  ProductDto convertToDto(Product entity);

  @IterableMapping(qualifiedByName = "convertToDtoWithoutLazyLoadingDiscounts")
  List<ProductDto> convertToDtoList(List<Product> entities);

  @Mapping(target = "discount", ignore = true)
  @Named("convertToDtoWithoutLazyLoadingDiscounts")
  ProductDto convertToDtoWithoutLazyLoadingDiscounts(Product entity);

  @Mapping(target = "product", ignore = true)
  ProductDiscount convertToEntity(ProductDiscountDto productDiscountDto);

  CartDto convertToDto(Cart cart);

  @Mapping(target = "product", qualifiedByName = "convertToDto")
  CartItemDto convertToDto(CartItem cartItem);

  static Amount map(BigDecimal value) {
    if (value == null) {
      return null;
    }

    return new Amount(value);
  }

  static BigDecimal map(Amount value) {
    if (value == null) {
      return null;
    }

    return value.getPrice();
  }
}
