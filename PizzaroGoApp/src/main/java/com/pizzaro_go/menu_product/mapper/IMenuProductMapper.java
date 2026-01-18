package com.pizzaro_go.menu_product.mapper;

import com.pizzaro_go.common.enums.ProductCategory;
import com.pizzaro_go.common.utils.StringUtils;
import com.pizzaro_go.menu_product.dtos.MenuProductRequest;
import com.pizzaro_go.menu_product.dtos.MenuProductResponse;
import com.pizzaro_go.menu_product.entity.MenuProduct;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper interface for converting between MenuProduct entity and DTOs.
 */
@Mapper(componentModel = "spring", imports = { ProductCategory.class, StringUtils.class })
public interface IMenuProductMapper {

    /**
     * Converts a MenuProductRequest DTO to a MenuProduct entity.
     *
     * @param request the menu product request DTO
     * @return the menu product entity
     */
    @Mapping(target = "productCategory", expression = "java(request.getProductCategory() != null ? ProductCategory.valueOf(request.getProductCategory().toUpperCase()) : null)")
    @Mapping(target = "stockUsages", ignore = true)
    MenuProduct toEntity(MenuProductRequest request);

    /**
     * Converts a MenuProduct entity to a MenuProductResponse DTO.
     *
     * @param menuProduct the menu product entity
     * @return the menu product response DTO
     */
    @Mapping(target = "productCategory", expression = "java(menuProduct.getProductCategory() != null ? StringUtils.capitalize(menuProduct.getProductCategory().name()) : null)")
    MenuProductResponse toResponse(MenuProduct menuProduct);

    /**
     * Converts a list of MenuProduct entities to a list of MenuProductResponse
     * DTOs.
     *
     * @param menuProducts the list of menu product entities
     * @return the list of menu product response DTOs
     */
    List<MenuProductResponse> toResponseList(List<MenuProduct> menuProducts);
}
