package com.pizzaro_go.oreder_item.mapper;

import com.pizzaro_go.common.enums.Status;
import com.pizzaro_go.common.utils.StringUtils;
import com.pizzaro_go.oreder_item.dtos.OrderItemRequest;
import com.pizzaro_go.oreder_item.dtos.OrderItemResponse;
import com.pizzaro_go.oreder_item.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for converting between OrderItem entities and OrderItem DTOs.
 */
@Mapper(componentModel = "spring", imports = { Status.class, StringUtils.class })
public interface IOrderItemMapper {

    /**
     * Converts an OrderItemRequest into an OrderItem entity.
     * The order and menuProduct fields are ignored and should be set by the
     * service.
     *
     * @param request the incoming order item data
     * @return the mapped OrderItem entity
     */
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "menuProduct", ignore = true)
    @Mapping(target = "status", expression = "java(request.getStatus() != null ? Status.valueOf(request.getStatus().toUpperCase()) : Status.PENDING)")
    OrderItem toEntity(OrderItemRequest request);

    /**
     * Converts an OrderItem entity into an OrderItemResponse.
     *
     * @param entity the OrderItem entity to convert
     * @return the mapped OrderItemResponse DTO
     */
    @Mapping(target = "orderId", expression = "java(entity.getOrder().getId())")
    @Mapping(target = "menuProductId", expression = "java(entity.getMenuProduct().getId())")
    @Mapping(target = "menuProductName", expression = "java(entity.getMenuProduct().getName())")
    @Mapping(target = "status", expression = "java(entity.getStatus() != null ? StringUtils.capitalize(entity.getStatus().name()) : null)")
    OrderItemResponse toResponse(OrderItem entity);
}
