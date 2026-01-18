package com.pizzaro_go.stock_item.mapper;

import com.pizzaro_go.common.enums.Category;
import com.pizzaro_go.common.enums.Unit;
import com.pizzaro_go.common.utils.StringUtils;
import com.pizzaro_go.stock_item.dtos.StockItemRequest;
import com.pizzaro_go.stock_item.dtos.StockItemResponse;
import com.pizzaro_go.fileimport.excel.entities.StockItemFileData;
import com.pizzaro_go.stock_item.entity.StockItem;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper interface for converting between StockItem entity and DTOs.
 */
@Mapper(componentModel = "spring", imports = { Unit.class, Category.class, StringUtils.class })
public interface IStockItemMapper {

    /**
     * Converts a StockRequest DTO to a StockItem entity.
     *
     * @param request the stock item request DTO
     * @return the stock item entity
     */
    @Mapping(target = "unit", expression = "java(request.getUnit() != null ? Unit.valueOf(request.getUnit().toUpperCase()) : null)")
    @Mapping(target = "category", expression = "java(request.getCategory() != null ? Category.valueOf(request.getCategory().toUpperCase()) : Category.INGREDIENT)")
    @Mapping(target = "usages", ignore = true)
    StockItem toEntity(StockItemRequest request);

    /**
     * Converts a StockItem entity to a StockResponse DTO.
     *
     * @param stockItem the stock item entity
     * @return the stock response DTO
     */
    @Mapping(target = "unit", expression = "java(stockItem.getUnit() != null ?StringUtils.capitalize(stockItem.getUnit().name()) : null)")
    @Mapping(target = "category", expression = "java(StringUtils.capitalize(stockItem.getCategory().name()))")
    StockItemResponse toResponse(StockItem stockItem);

    /**
     * Converts a list of StockItem entities to a list of StockResponse DTOs.
     *
     * @param stockItems the list of stock item entities
     * @return the list of stock response DTOs
     */
    List<StockItemResponse> toResponseList(List<StockItem> stockItems);

    /**
     * Converts a StockFileData object from Excel to a StockItem entity.
     *
     * @param stockFileData the stock file data
     * @return the stockItem entity
     */
    @Mapping(target = "unit", expression = "java(stockFileData.getUnit() != null ? Unit.valueOf(stockFileData.getUnit().toUpperCase()) : null)")
    @Mapping(target = "category", expression = "java(stockFileData.getCategory() != null ? Category.valueOf(stockFileData.getCategory().toUpperCase()) : Category.INGREDIENT)")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usages", ignore = true)
    StockItem toEntity(StockItemFileData stockFileData);
}
