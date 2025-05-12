package com.enterprise.backend.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_transactions")
public class Transaction extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String gateway;
    
    @Column(nullable = false, name = "transaction_date")
    private LocalDateTime transactionDate;
    
    @Column(name = "account_number", length = 100)
    private String accountNumber;
    
    @Column(name = "sub_account", length = 250)
    private String subAccount;
    
    @Column(name = "amount_in", precision = 20, scale = 2, nullable = false)
    private BigDecimal amountIn;
    
    @Column(name = "amount_out", precision = 20, scale = 2, nullable = false)
    private BigDecimal amountOut;
    
    @Column(precision = 20, scale = 2, nullable = false)
    private BigDecimal accumulated;
    
    @Column(length = 250)
    private String code;
    
    @Column(name = "transaction_content", columnDefinition = "TEXT")
    private String transactionContent;
    
    @Column(name = "reference_number")
    private String referenceNumber;
    
    @Column(columnDefinition = "TEXT")
    private String body;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        if (transactionDate == null) {
            transactionDate = LocalDateTime.now();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}