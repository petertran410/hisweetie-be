package com.enterprise.backend.service;

import com.enterprise.backend.exception.EnterpriseBackendException;
import com.enterprise.backend.model.entity.Auditable;
import com.enterprise.backend.model.entity.News;
import com.enterprise.backend.model.entity.QNews;
import com.enterprise.backend.model.error.ErrorCode;
import com.enterprise.backend.model.request.NewsRequest;
import com.enterprise.backend.model.request.SearchNewsRequest;
import com.enterprise.backend.model.response.NewsResponse;
import com.enterprise.backend.model.response.NewsSearchResponse;
import com.enterprise.backend.service.base.BaseService;
import com.enterprise.backend.service.repository.NewsRepository;
import com.enterprise.backend.service.transfomer.NewsTransformer;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NewsService extends BaseService<News, Long, NewsRepository, NewsTransformer, NewsRequest, NewsResponse> {
    private static final QNews qNews = QNews.news;
    private static final Map<String, ComparableExpressionBase<?>> sortProperties = new HashMap<>();

    protected NewsService(NewsRepository repo,
                          NewsTransformer transformer,
                          EntityManager em) {
        super(repo, transformer, em);
        sortProperties.put(News.Fields.id, qNews.id);
        sortProperties.put(Auditable.Fields.createdDate, qNews.createdDate);
        sortProperties.put(Auditable.Fields.updatedDate, qNews.updatedDate);
    }

    private void validateTitle(NewsRequest newsRequest) {
        if (repo.countByTitle(newsRequest.getTitle()) > 0) {
            throw new EnterpriseBackendException(ErrorCode.NEWS_CONFLICT);
        }
    }

    @Transactional
    public void createNews(NewsRequest newsRequest) {
        validateTitle(newsRequest);

        var news = transformer.toEntity(newsRequest);
        repo.save(news);
    }

    @Transactional
    public void updateNews(NewsRequest newsRequest, Long id) {
        var news = getOrElseThrow(id);

        if (!(news.getTitle().equals(newsRequest.getTitle()))) {
            validateTitle(newsRequest);
        }

        BeanUtils.copyProperties(newsRequest, news);
        repo.save(news);
    }

    @Transactional
    public void deleteNews(Long id) {
        var news = getOrElseThrow(id);
        repo.delete(news);
    }

    public NewsResponse getNewsById(Long newsId) {
        var news = getOrElseThrow(newsId);
        return transformer.toResponse(news);
    }

    public NewsResponse clientGetNewsById(Long newsId) {
        var news = getOrElseThrow(newsId);
        news.setView(news.getView() == null ? 1 : news.getView() + 1);
        return transformer.toResponse(repo.save(news));
    }

    public Page<NewsSearchResponse> search(SearchNewsRequest searchNewsRequest) {
        PageRequest of = PageRequest.of(searchNewsRequest.getPageNumber(), searchNewsRequest.getPageSize());
        JPAQuery<News> search = searchQuery(searchNewsRequest);
        log.info("searchNews query: {}", search);

        List<NewsSearchResponse> responses = search.fetch()
                .stream()
                .map(transformer::toSearchResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(responses, of, search.fetchCount());
    }

    private JPAQuery<News> searchQuery(SearchNewsRequest searchRequest) {
        JPAQuery<News> query = queryFactory.selectFrom(qNews);

        if (StringUtils.isNotEmpty(searchRequest.getTitle())) {
            query.where(qNews.title.containsIgnoreCase(searchRequest.getTitle()));
        }

        if (StringUtils.isNotEmpty(searchRequest.getType())) {
            query.where(qNews.type.eq(searchRequest.getType()));
        }

        queryPage(searchRequest, query);
        sortBy(searchRequest.getOrderBy(), searchRequest.getIsDesc(), query, sortProperties);
        return query;
    }

    @Override
    protected String notFoundMessage() {
        return "Not found news";
    }
}
