package com.tinder.product_cicd.web.mapper;

import com.tinder.product_cicd.product.Product;
import com.tinder.product_cicd.web.dto.ProductDto;

import java.util.List;
import java.util.stream.Collectors;

public class ProductMapper {

    public static ProductDto toDto(Product p) {
        if (p == null) return null;
        return new ProductDto(p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getCreatedAt());
    }

    public static Product toEntity(ProductDto dto) {
        if (dto == null) return null;
        Product p = new Product();
        p.setName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setPrice(dto.getPrice());
        return p;
    }

    public static void updateEntityFromDto(ProductDto dto, Product entity) {
        if (dto == null || entity == null) return;
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
    }

    public static List<ProductDto> toDtoList(List<Product> products) {
        return products.stream().map(ProductMapper::toDto).collect(Collectors.toList());
    }
}
