package com.enterprise.backend.service;

import com.enterprise.backend.exception.EnterpriseBackendException;
import com.enterprise.backend.model.entity.Application;
import com.enterprise.backend.model.entity.Auditable;
import com.enterprise.backend.model.entity.QApplicant;
import com.enterprise.backend.model.entity.QApplication;
import com.enterprise.backend.model.entity.QJobPost;
import com.enterprise.backend.model.error.ErrorCode;
import com.enterprise.backend.model.request.ApplicantRequest;
import com.enterprise.backend.model.request.ApplicationRequest;
import com.enterprise.backend.model.request.SearchApplicationRequest;
import com.enterprise.backend.model.response.ApplicationResponse;
import com.enterprise.backend.service.base.BaseService;
import com.enterprise.backend.service.repository.ApplicantRepository;
import com.enterprise.backend.service.repository.ApplicationRepository;
import com.enterprise.backend.service.repository.JobPostRepository;
import com.enterprise.backend.service.transfomer.ApplicantTransformer;
import com.enterprise.backend.service.transfomer.ApplicationTransformer;
import com.enterprise.backend.service.transfomer.JobPostTransformer;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ApplicationService extends BaseService<Application, Long, ApplicationRepository, ApplicationTransformer, ApplicationRequest, ApplicationResponse> {

    private static final QJobPost qJobPost = QJobPost.jobPost;
    private static final QApplicant qApplicant = QApplicant.applicant;
    private static final QApplication qApplication = QApplication.application;
    private static final Map<String, ComparableExpressionBase<?>> sortProperties = new HashMap<>();
    private final ApplicantTransformer applicantTransformer;
    private final JobPostTransformer jobPostTransformer;
    private final ApplicantRepository applicantRepository;
    private final JobPostRepository jobPostRepository;

    protected ApplicationService(ApplicationRepository repo,
                                 ApplicationTransformer transformer,
                                 EntityManager em,
                                 ApplicantTransformer applicantTransformer,
                                 JobPostTransformer jobPostTransformer,
                                 ApplicantRepository applicantRepository,
                                 JobPostRepository jobPostRepository) {
        super(repo, transformer, em);
        this.applicantTransformer = applicantTransformer;
        this.jobPostTransformer = jobPostTransformer;
        this.applicantRepository = applicantRepository;
        this.jobPostRepository = jobPostRepository;
        sortProperties.put(Application.Fields.id, qApplication.id);
        sortProperties.put(Auditable.Fields.createdDate, qApplication.createdDate);
        sortProperties.put(Auditable.Fields.updatedDate, qApplication.updatedDate);
    }

    private Optional<Application> findApplicationPending(ApplicantRequest applicantRequest) {
        return repo.findApplicationPending(applicantRequest.getEmail(), applicantRequest.getPhoneNumber(), applicantRequest.getJobId());
    }

    @Transactional
    public void applyCV(ApplicantRequest applicantRequest) {
        findApplicationPending(applicantRequest).ifPresentOrElse(
                application -> {
                    application.getApplicant().setResumeUrl(applicantRequest.getResumeUrl());
                    application.getApplicant().setName(applicantRequest.getName());
                    repo.save(application);
                },
                () -> {
                    var jobPost = jobPostRepository.findById(applicantRequest.getJobId())
                            .orElseThrow(() -> new EnterpriseBackendException(ErrorCode.JOB_POST_NOT_FOUND));

                    var applicant = applicantRepository.findByEmailAndPhoneNumber(
                            applicantRequest.getEmail(),
                            applicantRequest.getPhoneNumber()
                    ).orElseGet(() -> applicantRepository.save(applicantTransformer.toEntity(applicantRequest)));

                    var application = new Application();
                    application.setApplicant(applicant);
                    application.setJobPost(jobPost);
                    repo.save(application);
                }
        );
    }

    @Transactional
    public void changeStatusCVProfile(Long id, ApplicationRequest request) {
        var application = getOrElseThrow(id);
        application.setStatus(request.getStatus());
        application.setNote(request.getNote());
        repo.save(application);
    }

    public ApplicationResponse getApplicationById(Long jobPostId) {
        var jobPost = getOrElseThrow(jobPostId);
        return buildApplicationResp(jobPost);
    }

    public Page<ApplicationResponse> search(SearchApplicationRequest searchApplicationRequest) {
        PageRequest of = PageRequest.of(searchApplicationRequest.getPageNumber(), searchApplicationRequest.getPageSize());
        JPAQuery<Application> search = searchQuery(searchApplicationRequest);
        log.info("searchApplication query: {}", search);

        List<ApplicationResponse> responses = search.fetch()
                .stream()
                .map(this::buildApplicationResp)
                .collect(Collectors.toList());
        return new PageImpl<>(responses, of, search.fetchCount());
    }

    private ApplicationResponse buildApplicationResp(Application application) {
        var result = transformer.toResponse(application);
        result.setApplicant(applicantTransformer.toResponse(application.getApplicant()));
        result.setJobPost(jobPostTransformer.toResponse(application.getJobPost()));
        return result;
    }

    private JPAQuery<Application> searchQuery(SearchApplicationRequest searchApplicationRequest) {
        JPAQuery<Application> query = queryFactory.selectFrom(qApplication)
                .innerJoin(qApplication.applicant, qApplicant)
                .innerJoin(qApplication.jobPost, qJobPost);

        if (StringUtils.isNotEmpty(searchApplicationRequest.getLocation())) {
            query.where(qJobPost.location.containsIgnoreCase(searchApplicationRequest.getLocation()));
        }

        if (StringUtils.isNotEmpty(searchApplicationRequest.getTitle())) {
            query.where(qJobPost.title.containsIgnoreCase(searchApplicationRequest.getTitle()));
        }

        if (searchApplicationRequest.getEmploymentType() != null) {
            query.where(qJobPost.employmentType.eq(searchApplicationRequest.getEmploymentType()));
        }

        if (searchApplicationRequest.getWorkMode() != null) {
            query.where(qJobPost.workMode.eq(searchApplicationRequest.getWorkMode()));
        }

        if (searchApplicationRequest.getApplicationDeadline() != null) {
            query.where(qJobPost.applicationDeadline.goe(searchApplicationRequest.getApplicationDeadline()));
        }

        if (StringUtils.isNotEmpty(searchApplicationRequest.getName())) {
            query.where(qApplicant.name.containsIgnoreCase(searchApplicationRequest.getName()));
        }

        if (StringUtils.isNotEmpty(searchApplicationRequest.getEmail())) {
            query.where(qApplicant.email.containsIgnoreCase(searchApplicationRequest.getEmail()));
        }

        if (StringUtils.isNotEmpty(searchApplicationRequest.getPhoneNumber())) {
            query.where(qApplicant.phoneNumber.containsIgnoreCase(searchApplicationRequest.getPhoneNumber()));
        }

        if (searchApplicationRequest.getStatus() != null) {
            query.where(qApplication.status.eq(searchApplicationRequest.getStatus()));
        }

        queryPage(searchApplicationRequest, query);
        sortBy(searchApplicationRequest.getOrderBy(), searchApplicationRequest.getIsDesc(), query, sortProperties);
        return query;
    }

    @Override
    protected String notFoundMessage() {
        return "Not found product";
    }
}
