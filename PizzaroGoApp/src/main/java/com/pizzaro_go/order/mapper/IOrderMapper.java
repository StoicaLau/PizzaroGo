package com.pizzaro_go.order.mapper;

import com.pizzaro_go.order.dtos.OrderRequest;
import com.pizzaro_go.order.dtos.OrderResponse;
import com.pizzaro_go.order.entity.Order;
import org.mapstruct.*;

/**
 * Mapper for converting between Order entities and Order DTOs.
 */
@Mapper(componentModel = "spring")
public interface IOrderMapper {

    /**
     * Converts an OrderRequest into an Order entity.
     * The user field is ignored and set later in the service layer.
     *
     * @param request the incoming order data
     * @return the mapped Order entity
     */
    @Mapping(target = "user", ignore = true) // set it in service
    @Mapping(target = "status",
            expression = "java(request.getStatus() != null ? Status.valueOf(request.getStatus().toUpperCase()) : Status.PENDING)")
    Order toEntity(OrderRequest request);


    @Mapping(target = "userid", source = "user.id")
    @Mapping(target = "status", expression = "java(order.getStatus().name())")
    OrderResponse toResponse(Order order);
}
