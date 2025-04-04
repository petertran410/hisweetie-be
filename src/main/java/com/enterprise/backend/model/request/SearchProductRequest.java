package com.enterprise.backend.model.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class SearchProductRequest extends SearchRequest {
    private Long productId;
    private Set<Long> categoryId;
    private String title;
    private Long fromPrice;
    private Long toPrice;
    private String type;
    private Boolean isFeatured;
}
