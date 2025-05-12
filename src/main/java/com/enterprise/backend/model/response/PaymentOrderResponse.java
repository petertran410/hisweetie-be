// Create a new file at: src/main/java/com/enterprise/backend/model/response/PaymentOrderResponse.java
package com.enterprise.backend.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrderResponse {
    private Long id;
    private BigDecimal total;
    private String name;
    private String paymentStatus;
    private LocalDateTime createdAt;
}