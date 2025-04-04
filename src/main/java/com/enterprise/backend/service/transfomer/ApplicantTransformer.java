package com.enterprise.backend.service.transfomer;

import com.enterprise.backend.model.entity.Applicant;
import com.enterprise.backend.model.request.ApplicantRequest;
import com.enterprise.backend.model.response.ApplicantResponse;
import com.enterprise.backend.service.base.BaseTransformer;
import org.mapstruct.Mapper;

@Mapper
public interface ApplicantTransformer extends BaseTransformer<Applicant, ApplicantResponse, ApplicantRequest> {
}
