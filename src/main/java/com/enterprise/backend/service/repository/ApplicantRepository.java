package com.enterprise.backend.service.repository;

import com.enterprise.backend.model.entity.Applicant;
import com.enterprise.backend.service.base.BaseCommonRepository;

import java.util.Optional;

public interface ApplicantRepository extends BaseCommonRepository<Applicant, Long> {
    Optional<Applicant> findByEmailAndPhoneNumber(String email, String phoneNumber);
}
