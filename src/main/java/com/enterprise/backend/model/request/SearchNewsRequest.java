package com.enterprise.backend.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchNewsRequest extends SearchRequest {
    private String title;
    private String type;
}
