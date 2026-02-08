package com.pizzaro_go.menu_product.mapper;

import com.pizzaro_go.common.enums.ProductCategory;
import com.pizzaro_go.common.utils.StringUtils;
import com.pizzaro_go.fileimport.excel.entities.MenuProductFileData;
import com.pizzaro_go.menu_product.dtos.MenuProductRequest;
import com.pizzaro_go.menu_product.dtos.MenuProductResponse;
import com.pizzaro_go.menu_product.entity.MenuProductEntity;
import com.pizzaro_go.product_stock_usage.mapper.IProductStockUsageMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper interface for converting between MenuProduct entity and DTOs.
 */
@Mapper(componentModel = "spring", uses = IProductStockUsageMapper.class, imports = { ProductCategory.class,
        StringUtils.class })
public interface IMenuProductMapper {

    /**
     * Converts a MenuProductRequest DTO to a MenuProduct entity.
     *
     * @param request the menu product request DTO
     * @return the menu product entity
     */
    @Mapping(target = "productCategory", expression = "java(request.getProductCategory() != null ? ProductCategory.valueOf(request.getProductCategory().toUpperCase()) : null)")
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "productStockUsages", ignore = true)
    MenuProductEntity toEntity(MenuProductRequest request);

    /**
     * Converts a MenuProduct entity to a MenuProductResponse DTO.
     *
     * @param menuProduct the menu product entity
     * @return the menu product response DTO
     */
    @Mapping(target = "productCategory", expression = "java(menuProduct.getProductCategory() != null ? StringUtils.capitalize(menuProduct.getProductCategory().name()) : null)")
    @Mapping(target = "stockUsages", source = "productStockUsages")
    MenuProductResponse toResponse(MenuProductEntity menuProduct);

    /**
     * Converts a list of MenuProduct entities to a list of MenuProductResponse
     * DTOs.
     *
     * @param menuProducts the list of menu product entities
     * @return the list of menu product response DTOs
     */
    List<MenuProductResponse> toResponseList(List<MenuProductEntity> menuProducts);

    /**
     * Converts a MenuProductFileData object from Excel to a MenuProduct entity.
     *
     * @param fileData the menu product file data from Excel
     * @return the mapped MenuProduct entity
     */
    @Mapping(target = "productCategory", expression = "java(fileData.getCategory() != null ? ProductCategory.valueOf(fileData.getCategory().toUpperCase()) : null)")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "productStockUsages", ignore = true)
    MenuProductEntity toEntity(MenuProductFileData fileData);
}
