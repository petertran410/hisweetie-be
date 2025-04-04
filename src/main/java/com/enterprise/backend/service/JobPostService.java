package com.enterprise.backend.service;

import com.enterprise.backend.exception.EnterpriseBackendException;
import com.enterprise.backend.model.entity.Auditable;
import com.enterprise.backend.model.entity.JobPost;
import com.enterprise.backend.model.entity.QJobPost;
import com.enterprise.backend.model.error.ErrorCode;
import com.enterprise.backend.model.request.JobPostRequest;
import com.enterprise.backend.model.request.SearchJobPostRequest;
import com.enterprise.backend.model.response.JobPostResponse;
import com.enterprise.backend.service.base.BaseService;
import com.enterprise.backend.service.repository.JobPostRepository;
import com.enterprise.backend.service.transfomer.JobPostTransformer;
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
public class JobPostService extends BaseService<JobPost, Long, JobPostRepository, JobPostTransformer, JobPostRequest, JobPostResponse> {

    private static final QJobPost qJobPost = QJobPost.jobPost;
    private static final Map<String, ComparableExpressionBase<?>> sortProperties = new HashMap<>();


    protected JobPostService(JobPostRepository repo,
                             JobPostTransformer transformer,
                             EntityManager em) {
        super(repo, transformer, em);
        sortProperties.put(JobPost.Fields.id, qJobPost.id);
        sortProperties.put(Auditable.Fields.createdDate, qJobPost.createdDate);
        sortProperties.put(Auditable.Fields.updatedDate, qJobPost.updatedDate);
    }

    private void validateTitle(JobPostRequest jobPostRequest) {
        if (repo.countByTitle(jobPostRequest.getTitle()) > 0) {
            throw new EnterpriseBackendException(ErrorCode.JOB_POST_CONFLICT);
        }
    }

    @Transactional
    public void createJobPost(JobPostRequest jobPostRequest) {
        validateTitle(jobPostRequest);

        var jobPost = transformer.toEntity(jobPostRequest);
        repo.save(jobPost);
    }

    @Transactional
    public void updateJobPost(JobPostRequest jobPostRequest, Long id) {
        var jobPost = getOrElseThrow(id);

        if (!(jobPost.getTitle().equals(jobPostRequest.getTitle()))) {
            validateTitle(jobPostRequest);
        }

        BeanUtils.copyProperties(jobPostRequest, jobPost);
        repo.save(jobPost);
    }

    @Transactional
    public void deleteJobPost(Long id) {
        var jobPost = getOrElseThrow(id);
        repo.delete(jobPost);
    }

    public JobPostResponse getJobPostById(Long jobPostId) {
        var jobPost = getOrElseThrow(jobPostId);
        return transformer.toResponse(jobPost);
    }

    public Page<JobPostResponse> search(SearchJobPostRequest searchJobPostRequest) {
        PageRequest of = PageRequest.of(searchJobPostRequest.getPageNumber(), searchJobPostRequest.getPageSize());
        JPAQuery<JobPost> search = searchQuery(searchJobPostRequest);
        log.info("searchJobPost query: {}", search);

        List<JobPostResponse> responses = search.fetch()
                .stream()
                .map(transformer::toResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(responses, of, search.fetchCount());
    }

    private JPAQuery<JobPost> searchQuery(SearchJobPostRequest searchJobPostRequest) {
        JPAQuery<JobPost> query = queryFactory.selectFrom(qJobPost);

        if (StringUtils.isNotEmpty(searchJobPostRequest.getTitle())) {
            query.where(qJobPost.title.containsIgnoreCase(searchJobPostRequest.getTitle()));
        }

        if (StringUtils.isNotEmpty(searchJobPostRequest.getLocation())) {
            query.where(qJobPost.location.containsIgnoreCase(searchJobPostRequest.getLocation()));
        }

        if (searchJobPostRequest.getWorkMode() != null) {
            query.where(qJobPost.workMode.eq(searchJobPostRequest.getWorkMode()));
        }

        if (searchJobPostRequest.getEmploymentType() != null) {
            query.where(qJobPost.employmentType.eq(searchJobPostRequest.getEmploymentType()));
        }

        if (searchJobPostRequest.getApplicationDeadline() != null) {
            query.where(qJobPost.applicationDeadline.goe(searchJobPostRequest.getApplicationDeadline()));
        }

        queryPage(searchJobPostRequest, query);
        sortBy(searchJobPostRequest.getOrderBy(), searchJobPostRequest.getIsDesc(), query, sortProperties);
        return query;
    }

    @Override
    protected String notFoundMessage() {
        return "Not found product";
    }
}
