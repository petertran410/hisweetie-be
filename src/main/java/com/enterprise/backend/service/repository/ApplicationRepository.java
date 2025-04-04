package com.enterprise.backend.service.repository;

import com.enterprise.backend.model.entity.Application;
import com.enterprise.backend.service.base.BaseCommonRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ApplicationRepository extends BaseCommonRepository<Application, Long> {
    @Query(value = "SELECT * FROM application a " +
            "JOIN applicant ap ON a.applicant_id = ap.id " +
            "JOIN job_post jp ON a.job_post_id = jp.id " +
            "WHERE ap.email = :email " +
            "AND ap.phone_number = :phoneNumber " +
            "AND jp.id = :jobId " +
            "AND a.status = 'PENDING'", nativeQuery = true)
    Optional<Application> findApplicationPending(
            @Param("email") String email,
            @Param("phoneNumber") String phoneNumber,
            @Param("jobId") Long jobId);
}
