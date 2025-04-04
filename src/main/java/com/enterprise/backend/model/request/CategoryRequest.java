package com.enterprise.backend.model.request;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CategoryRequest {
    @NotEmpty(message = "name is required!")
    private String name;

    @Min(value = 0, message = "Thứ tự ưu tiên không thể âm!")
    private Integer priority;

    private Long parentId;

    @NotNull(message = "Ảnh đại diện không được để trống!")
    private List<String> imagesUrl;

    @NotEmpty(message = "Mô tả không được để trống!")
    private String description;
}
