package com.enterprise.backend.model.request;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class CategoriesRequest {
    @NotNull(message = "priority is required!")
    @Min(value = 0, message = "Thứ tự ưu tiên không thể âm!")
    private Integer priority;

    @NotNull(message = "id is required!")
    private Long id;
}
