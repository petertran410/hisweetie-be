package com.enterprise.backend.model.response;

import lombok.Data;

import java.util.List;

@Data
public class CategoryResponse {
    private Long id;
    private String name;
    private Integer priority;
    private List<String> imagesUrl;
    private String description;
    private Long parentId;
    private String parentName;
    private List<CategoryResponse> children;
}
