package com.enterprise.backend.model.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class NewsRequest {
    @NotNull
    private List<String> imagesUrl;

    @NotEmpty
    private String title;

    private String description;

    private String htmlContent;

    private String type;
}
