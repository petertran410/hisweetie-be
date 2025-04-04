package com.enterprise.backend.model.response;

import lombok.Data;

import java.util.List;

@Data
public class ProductResponse {
    private Long id;
    private String title;
    private String description;
    private String generalDescription;
    private String instruction;
    private Boolean isFeatured;
    private String type;
    private List<String> imagesUrl;
    private Long price;
    private Long quantity;
    private Double rate;
    private String createdBy;
    private String updatedBy;
    private String createdDate;
    private String updatedDate;
    private List<OfCategory> ofCategories;
    private boolean isFavorite;
    private String featuredThumbnail;
    private String recipeThumbnail;
    @Data
    public static class OfCategory {
        private Long id;
        private String name;
        private Integer priority;
    }
}
