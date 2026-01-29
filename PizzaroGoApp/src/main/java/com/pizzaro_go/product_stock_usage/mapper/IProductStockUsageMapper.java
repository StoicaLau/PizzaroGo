package com.pizzaro_go.product_stock_usage.mapper;

import com.pizzaro_go.product_stock_usage.dtos.ProductStockUsageRequest;
import com.pizzaro_go.product_stock_usage.dtos.ProductStockUsageResponse;
import com.pizzaro_go.product_stock_usage.entity.ProductStockUsageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for converting between ProductStockUsage entities and
 * ProductStockUsage DTOs.
 */
@Mapper(componentModel = "spring")
public interface IProductStockUsageMapper {

    /**
     * Converts a ProductStockUsageRequest into a ProductStockUsage entity.
     * The menuProduct and stockItem fields are ignored and set later in the service
     * layer.
     *
     * @param request the incoming product stock usage data
     * @return the mapped ProductStockUsage entity
     */
    @Mapping(target = "menuProduct", ignore = true)
    @Mapping(target = "stockItem", ignore = true)
    ProductStockUsageEntity toEntity(ProductStockUsageRequest request);

    /**
     * Converts a ProductStockUsage entity into a ProductStockUsageResponse.
     *
     * @param entity the ProductStockUsage entity to convert
     * @return the mapped ProductStockUsageResponse DTO
     */
    @Mapping(target = "menuProductId", expression = "java(entity.getMenuProduct().getId())")
    @Mapping(target = "stockItemId", expression = "java(entity.getStockItem().getId())")
    ProductStockUsageResponse toResponse(ProductStockUsageEntity entity);

}
