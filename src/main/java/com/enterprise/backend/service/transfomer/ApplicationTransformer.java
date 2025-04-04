package com.enterprise.backend.service.transfomer;

import com.enterprise.backend.model.entity.Application;
import com.enterprise.backend.model.request.ApplicationRequest;
import com.enterprise.backend.model.response.ApplicationResponse;
import com.enterprise.backend.service.base.BaseTransformer;
import org.mapstruct.Mapper;

@Mapper
public interface ApplicationTransformer extends BaseTransformer<Application, ApplicationResponse, ApplicationRequest> {
}
