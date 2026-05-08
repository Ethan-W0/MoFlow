package com.ran.hub.service.space;

import com.ran.commons.response.ApiResult;
import com.ran.commons.dto.space.SpaceAddDTO;
import com.ran.commons.dto.space.SpaceUpdateDTO;
import com.ran.commons.entity.space.Space;

public interface SpaceBizService {

    ApiResult<Long> create(SpaceAddDTO spaceAddDTO, Long enterpriseId);

    ApiResult<String> deleteSpace(Long spaceId, String mobile, String verifyCode);

    ApiResult<String> updateSpace(SpaceUpdateDTO spaceUpdateDTO);

    ApiResult<Space> visitSpace(Long spaceId);

    ApiResult<String> sendMessageCode(Long spaceId);

    ApiResult<Boolean> ossVersionUserUpgrade();
}
