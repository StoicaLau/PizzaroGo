package com.pizzaro_go.stock.mapper;

import com.pizzaro_go.common.enums.Unit;
import com.pizzaro_go.stock.dtos.StockRequest;
import com.pizzaro_go.stock.dtos.StockResponse;
import com.pizzaro_go.stock.entity.Stock;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper interface for converting between Stock entity and DTOs.
 */
@Mapper(componentModel = "spring", imports = {Unit.class})
public interface IStockMapper {

    /**
     * Converts a StockRequest DTO to a Stock entity.
     *
     * @param request the stock request DTO
     * @return the stock entity
     */
    @Mapping(target = "unit", expression = "java(request.getUnit() != null ? Unit.valueOf(request.getUnit().toUpperCase()) : null)")
    Stock toEntity(StockRequest request);

    /**
     * Converts a Stock entity to a StockResponse DTO.
     *
     * @param stock the stock entity
     * @return the stock response DTO
     */
    @Mapping(target = "unit", expression = "java(stock.getUnit() != null ? stock.getUnit().name() : null)")
    StockResponse toResponse(Stock stock);

    /**
     * Converts a list of Stock entities to a list of StockResponse DTOs.
     *
     * @param stocks the list of stock entities
     * @return the list of stock response DTOs
     */
    List<StockResponse> toResponseList(List<Stock> stocks);
}
