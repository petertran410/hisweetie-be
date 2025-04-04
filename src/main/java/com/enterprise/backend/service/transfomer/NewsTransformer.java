package com.enterprise.backend.service.transfomer;

import com.enterprise.backend.model.entity.News;
import com.enterprise.backend.model.request.NewsRequest;
import com.enterprise.backend.model.response.NewsResponse;
import com.enterprise.backend.model.response.NewsSearchResponse;
import com.enterprise.backend.service.base.BaseTransformer;
import org.mapstruct.Mapper;

@Mapper
public interface NewsTransformer extends BaseTransformer<News, NewsResponse, NewsRequest> {
    NewsSearchResponse toSearchResponse(News news);
}
