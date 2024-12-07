package com.chacha.controller;

import com.chacha.domain.Order;
import com.chacha.domain.OrderOption;
import com.chacha.domain.User;
import com.chacha.dto.OrderDTO;
import com.chacha.event.OrderEvent;
import com.chacha.service.KafkaMessageService;
import com.chacha.service.OrderService;
import com.google.gson.Gson;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final KafkaMessageService kafkaMessageService;
    private final Gson gson;

    public OrderController(OrderService orderService,KafkaMessageService kafkaMessageService, Gson gson) {
        this.orderService = orderService;
        this.kafkaMessageService = kafkaMessageService;
        this.gson = gson;
    }

    // 주문 추가
    @PostMapping
    public ResponseEntity<Order> createOrder(@AuthenticationPrincipal User loggedInUser,
                                             @RequestBody OrderOption orderOption) {
        Order order = orderService.createOrder(loggedInUser, orderOption);

        // Order 이벤트 구성
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
        Date now = new Date();
        OrderEvent event = new OrderEvent();
        event.setOrderTime(sdfDate.format(now));
        event.setOrderNumber(order.getOrderNumber());
        event.setUserId(loggedInUser.getId());
        event.setOptions(List.of(orderOption));

        kafkaMessageService.sendOrderEvent(gson.toJson(event));

        return ResponseEntity.ok(order);
    }

    // 주문 내역 조회
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getOrders(@AuthenticationPrincipal User loggedInUser) {
        List<Order> orders = orderService.getOrdersByUser(loggedInUser);
        List<OrderDTO> orderDTOs = orders.stream()
                .map(OrderDTO::new) // 각 Order 객체를 OrderDTO 객체로 변환
                .collect(Collectors.toList()); // 변환된 OrderDTO 객체를 새로운 리스트로 수집
        return ResponseEntity.ok(orderDTOs); // 상태코드 200과 함께 orderDTOs를 반환
    }

    // 주문 삭제
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@AuthenticationPrincipal User loggedInUser,
                                            @PathVariable Long orderId) {
        orderService.deleteOrder(loggedInUser, orderId);
        return ResponseEntity.noContent().build();
    }
}
