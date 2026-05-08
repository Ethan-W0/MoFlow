package com.ran.hub.service.space;

import com.ran.commons.response.ApiResult;

public interface ApplyRecordBizService {

    ApiResult<String> joinEnterpriseSpace(Long spaceId);

    ApiResult<String> agreeEnterpriseSpace(Long applyId);

    ApiResult<String> refuseEnterpriseSpace(Long applyId);
}
