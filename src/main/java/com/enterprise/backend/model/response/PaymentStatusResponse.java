// Create a new file at: src/main/java/com/enterprise/backend/model/response/PaymentStatusResponse.java
package com.enterprise.backend.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatusResponse {
    private String paymentStatus;
}