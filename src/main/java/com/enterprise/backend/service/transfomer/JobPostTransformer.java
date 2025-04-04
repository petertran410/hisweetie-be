package com.enterprise.backend.service.transfomer;

import com.enterprise.backend.model.entity.JobPost;
import com.enterprise.backend.model.request.JobPostRequest;
import com.enterprise.backend.model.response.JobPostResponse;
import com.enterprise.backend.service.base.BaseTransformer;
import org.mapstruct.Mapper;

@Mapper
public interface JobPostTransformer extends BaseTransformer<JobPost, JobPostResponse, JobPostRequest> {
}
