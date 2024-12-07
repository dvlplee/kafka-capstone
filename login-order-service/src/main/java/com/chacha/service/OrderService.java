package com.chacha.service;

import com.chacha.domain.Order;
import com.chacha.domain.OrderOption;
import com.chacha.domain.User;
import com.chacha.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // 주문 생성
    public Order createOrder(User user, OrderOption orderOption) {
        Order order = Order.builder()
                .orderNumber("ORD" + System.currentTimeMillis())
                .orderDate(LocalDateTime.now())
                .user(user)
                .orderOption(orderOption)
                .build();

        return orderRepository.save(order);
    }

    // 특정 사용자의 주문 내역 조회
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }

    // 주문 삭제
    public void deleteOrder(User user, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("You cannot delete this order");
        }

        orderRepository.delete(order);
    }
}
