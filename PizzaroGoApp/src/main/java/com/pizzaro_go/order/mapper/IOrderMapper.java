package com.pizzaro_go.order.mapper;

import com.pizzaro_go.common.enums.Status;
import com.pizzaro_go.common.utils.StringUtils;
import com.pizzaro_go.order.dtos.OrderRequest;
import com.pizzaro_go.order.dtos.OrderResponse;
import com.pizzaro_go.order.entity.OrderEntity;
import com.pizzaro_go.oreder_item.mapper.IOrderItemMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * Mapper for converting between Order entities and Order DTOs.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, imports = {
        Status.class, StringUtils.class }, uses = IOrderItemMapper.class)
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
    OrderEntity toEntity(OrderRequest request);

    /**
     * Converts an Order entity into an OrderResponse.
     *
     * @param order the Order entity to convert
     * @return the mapped OrderResponse DTO
     */
    @Mapping(target = "userId", expression = "java(order.getUser().getId())")
    @Mapping(target = "status", expression = "java(order.getStatus() != null ? StringUtils.capitalize(order.getStatus().name()) : null)")
    OrderResponse toResponse(OrderEntity order);
}
