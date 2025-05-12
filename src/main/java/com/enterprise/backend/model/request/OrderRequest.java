// Fix the existing src/main/java/com/enterprise/backend/model/request/OrderRequest.java
package com.enterprise.backend.model.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderRequest {
    private BigDecimal total;
    private String name;
}