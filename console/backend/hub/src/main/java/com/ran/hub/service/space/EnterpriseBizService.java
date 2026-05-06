package com.ran.hub.service.space;

import com.ran.commons.response.ApiResult;

public interface EnterpriseBizService {
    ApiResult<Boolean> visitEnterprise(Long enterpriseId);

    ApiResult<Long> create(EnterpriseAddDTO enterpriseAddDTO);

    ApiResult<String> updateName(String name);

    ApiResult<String> updateLogo(String logoUrl);

    ApiResult<String> updateAvatar(String avatarUrl);
}
