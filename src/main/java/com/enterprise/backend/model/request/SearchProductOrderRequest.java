package com.enterprise.backend.model.request;

import com.enterprise.backend.model.enums.OrderStatus;
import com.enterprise.backend.model.enums.OrderTypeStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchProductOrderRequest extends SearchRequest {
    private Long id;
    private String receiverFullName;
    private String email;
    private String phoneNumber;
    private String addressDetail;
    private OrderStatus status;
    private OrderTypeStatus type;
}
