package com.enterprise.backend.service.repository;

import com.enterprise.backend.model.entity.News;
import com.enterprise.backend.service.base.BaseCommonRepository;

import java.util.List;

public interface NewsRepository extends BaseCommonRepository<News, Long> {
    long countByTitle(String title);

    List<News> findAllByOrderByUpdatedDateDesc();
}