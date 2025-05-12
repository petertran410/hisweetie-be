// Update PaymentController.java
package com.enterprise.backend.controller;

import com.enterprise.backend.model.entity.TbOrder;
import com.enterprise.backend.model.request.OrderRequest;
import com.enterprise.backend.model.response.PaymentOrderResponse;
import com.enterprise.backend.model.response.PaymentStatusResponse;
import com.enterprise.backend.service.PaymentService;
import com.enterprise.backend.service.repository.TbOrderRepository;  // Add this import
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Log4j2
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {
    
    private final PaymentService paymentService;
    private final TbOrderRepository tbOrderRepository;
    
    @PostMapping("/create-order")
    public ResponseEntity<PaymentOrderResponse> createOrder(@RequestBody OrderRequest request) {
        TbOrder order = TbOrder.builder()
                .total(request.getTotal())
                .name(request.getName() != null ? request.getName() : "Default Product")
                .paymentStatus(TbOrder.PaymentStatus.Unpaid)
                .createdAt(LocalDateTime.now())
                .build();
        
        TbOrder savedOrder = tbOrderRepository.save(order);
        
        PaymentOrderResponse response = PaymentOrderResponse.builder()
                .id(savedOrder.getId())
                .total(savedOrder.getTotal())
                .name(savedOrder.getName())
                .paymentStatus(savedOrder.getPaymentStatus().name())
                .createdAt(savedOrder.getCreatedAt())
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/status/{orderId}")
    public ResponseEntity<PaymentStatusResponse> getPaymentStatus(@PathVariable Long orderId) {
        TbOrder.PaymentStatus status = paymentService.getOrderPaymentStatus(orderId);
        
        if (status == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(new PaymentStatusResponse(status.name()));
    }
    
    @GetMapping("/orders/{id}")
    public ResponseEntity<PaymentOrderResponse> getOrderDetails(@PathVariable Long id) {
        return tbOrderRepository.findById(id)
                .map(order -> {
                    PaymentOrderResponse response = PaymentOrderResponse.builder()
                            .id(order.getId())
                            .total(order.getTotal())
                            .name(order.getName())
                            .paymentStatus(order.getPaymentStatus().name())
                            .createdAt(order.getCreatedAt())
                            .build();
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}