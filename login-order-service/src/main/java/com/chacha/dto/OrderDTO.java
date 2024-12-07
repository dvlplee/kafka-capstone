package com.chacha.dto;

import com.chacha.domain.Order;
import com.chacha.domain.OrderOption;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class OrderDTO {
    private Long id;
    private String orderNumber;
    private LocalDateTime orderDate;
    private OrderOption orderOption; // 임베디드 옵션

    public OrderDTO(Order order) {
        this.id = order.getId();
        this.orderNumber = order.getOrderNumber();
        this.orderDate = order.getOrderDate();
        this.orderOption = order.getOrderOption();
    }
}
