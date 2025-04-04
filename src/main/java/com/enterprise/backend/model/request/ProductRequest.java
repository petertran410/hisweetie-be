package com.enterprise.backend.model.request;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Data
public class ProductRequest {
    @NotEmpty(message = "title is required!")
    private String title;

    @NotEmpty(message = "description is required!")
    private String description;

    private String generalDescription;

    private String instruction;

    private Boolean isFeatured;

    private String featuredThumbnail;

    private String recipeThumbnail;

    @NotNull(message = "imagesUrl is required!")
    private List<String> imagesUrl;

    @Min(value = 0, message = "Giá tiền không thể âm!")
    private Long price;

    @Min(value = 0, message = "Số lượng không thể âm!")
    private Long quantity;

    private Set<Long> categoryIds;

    private String type;
}
