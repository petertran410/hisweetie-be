package com.enterprise.backend.service.repository;

import com.enterprise.backend.model.entity.JobPost;
import com.enterprise.backend.service.base.BaseCommonRepository;

public interface JobPostRepository extends BaseCommonRepository<JobPost, Long> {
    Integer countByTitle(String title);
}
