package com.chacha.event;

import com.chacha.domain.OrderOption;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderEvent {
    private String orderTime;
    private String orderNumber;
    private Long userId;
    private List<OrderOption> options;
}
