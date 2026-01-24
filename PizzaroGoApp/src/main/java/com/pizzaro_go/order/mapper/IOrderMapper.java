package com.pizzaro_go.order.mapper;

import com.pizzaro_go.order.dtos.OrderRequest;
import com.pizzaro_go.order.dtos.OrderResponse;
import com.pizzaro_go.order.entity.Order;
import com.pizzaro_go.common.enums.Status;
import org.mapstruct.*;

/**
 * Mapper for converting between Order entities and Order DTOs.
 */
@Mapper(componentModel = "spring", imports = { Status.class })
public interface IOrderMapper {

    /**
     * Converts an OrderRequest into an Order entity.
     * The user field is ignored and set later in the service layer.
     *
     * @param request the incoming order data
     * @return the mapped Order entity
     */
    @Mapping(target = "user", ignore = true) // set it in service
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "status", expression = "java(request.getStatus() != null ? Status.valueOf(request.getStatus().toUpperCase()) : Status.PENDING)")
    Order toEntity(OrderRequest request);

    /**
     * Converts an Order entity into an OrderResponse.
     *
     * @param order the Order entity to convert
     * @return the mapped OrderResponse DTO
     */
    @Mapping(target = "userId", expression = "java(order.getUser().getId())")
    @Mapping(target = "status", expression = "java(order.getStatus().name())")
    OrderResponse toResponse(Order order);
}
