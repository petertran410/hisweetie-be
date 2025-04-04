package com.enterprise.backend.model.request;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
public class ProductOrderRequest {
    private String receiverFullName;
    @NotEmpty(message = "email is required!")
    @Pattern(regexp = "^[\\w.-]+@[\\w-]+(\\.[\\w-]{2,4}){1,4}$", message = "invalid email!")
    private String email;
    @NotEmpty(message = "phoneNumber is required!")
    @Pattern(regexp = "^[0-9+\\-]{9,15}$", message = "invalid phone number!")
    private String phoneNumber;
    private String addressDetail;
    private String note;
    private String userId;

    @NotNull(message = "products is required!")
    private List<@Valid OrderRequest> products;
    @NotNull(message = "htmlContent is required!")
    private String htmlContent;

    @Data
    public static class OrderRequest {
        @NotNull(message = "productId is required!")
        private Long productId;
        @NotNull(message = "quantity is required!")
        private Integer quantity;
    }
}
