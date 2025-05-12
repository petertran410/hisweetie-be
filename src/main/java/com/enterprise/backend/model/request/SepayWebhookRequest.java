// SepayWebhookRequest.java
package com.enterprise.backend.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SepayWebhookRequest {
    private Long id;
    private String gateway;
    
    @JsonProperty("transactionDate")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime transactionDate;
    
    @JsonProperty("accountNumber")
    private String accountNumber;
    
    @JsonProperty("subAccount")
    private String subAccount;
    
    private String content;
    
    @JsonProperty("transferType")
    private String transferType;
    
    @JsonProperty("transferAmount")
    private BigDecimal transferAmount;
    
    private BigDecimal accumulated;
    
    private String code;
    
    @JsonProperty("referenceCode")
    private String referenceNumber;
    
    private String description;
}