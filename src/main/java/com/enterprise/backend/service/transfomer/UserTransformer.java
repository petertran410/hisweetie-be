package com.enterprise.backend.service.transfomer;

import com.enterprise.backend.model.entity.User;
import com.enterprise.backend.model.request.SyncUserRequest;
import com.enterprise.backend.model.request.UserRequest;
import com.enterprise.backend.model.response.UserResponse;
import com.enterprise.backend.service.base.BaseTransformer;
import org.mapstruct.Mapper;

@Mapper
public interface UserTransformer extends BaseTransformer<User, UserResponse, UserRequest> {
    User toUser(SyncUserRequest request);

    SyncUserRequest toSyncUserRequest(User user);
}
