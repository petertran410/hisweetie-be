package com.enterprise.backend.model.response;

import lombok.Data;

import java.util.List;

@Data
public class NewsSearchResponse {
    private Long id;
    private String createdDate;
    private String updatedDate;
    private String createdBy;
    private String updatedBy;
    private List<String> imagesUrl;
    private String title;
    private String description;
    private Integer view;
    private String type;
}
