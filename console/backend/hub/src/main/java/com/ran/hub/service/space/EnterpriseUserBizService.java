package com.ran.hub.service.space;

import com.ran.commons.response.ApiResult;
import com.ran.commons.dto.space.UserLimitVO;

public interface EnterpriseUserBizService {

    ApiResult<String> remove(String uid);

    ApiResult<String> updateRole(String uid, Integer role);

    ApiResult<String> quitEnterprise();

    UserLimitVO getUserLimit(Long enterpriseId);
}
