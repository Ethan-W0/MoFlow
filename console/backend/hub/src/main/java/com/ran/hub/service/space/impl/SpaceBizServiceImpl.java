package com.ran.hub.service.space.impl;

import com.ran.commons.constant.ResponseEnum;
import com.ran.commons.data.UserInfoDataService;
import com.ran.commons.entity.user.UserInfo;
import com.ran.commons.exception.BusinessException;
import com.ran.commons.response.ApiResult;
import com.ran.commons.util.RequestContextUtil;
import com.ran.commons.dto.space.SpaceAddDTO;
import com.ran.commons.dto.space.SpaceUpdateDTO;
import com.ran.commons.entity.space.Enterprise;
import com.ran.commons.entity.space.EnterpriseUser;
import com.ran.commons.entity.space.Space;
import com.ran.commons.entity.space.SpaceUser;
import com.ran.commons.enums.space.EnterpriseRoleEnum;
import com.ran.commons.enums.space.EnterpriseServiceTypeEnum;
import com.ran.commons.enums.space.SpaceRoleEnum;
import com.ran.commons.enums.space.SpaceTypeEnum;
import com.ran.commons.service.space.EnterpriseService;
import com.ran.commons.service.space.EnterpriseUserService;
import com.ran.commons.service.space.SpaceService;
import com.ran.commons.service.space.SpaceUserService;
import com.ran.commons.util.space.OrderInfoUtil;
import com.ran.commons.util.space.SpaceInfoUtil;
import com.ran.hub.properties.SpaceLimitProperties;
import com.ran.hub.service.space.SpaceBizService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Slf4j
public class SpaceBizServiceImpl implements SpaceBizService {
    @Autowired
    private SpaceUserService spaceUserService;
    @Autowired
    private EnterpriseUserService enterpriseUserService;
    @Autowired
    private EnterpriseService enterpriseService;
    @Autowired(required = false)
    private SpaceLimitProperties spaceLimitProperties;
    @Autowired
    private SpaceService spaceService;
    @Autowired
    private UserInfoDataService userInfoDataService;

    @Override
    @Transactional
    public ApiResult<Long> create(SpaceAddDTO spaceAddDTO, Long enterpriseId) {
        if (spaceService.checkExistByName(spaceAddDTO.getName(), null)) {
            return ApiResult.error(ResponseEnum.SPACE_NAME_EXISTS);
        }
        Space space = new Space();
        BeanUtils.copyProperties(spaceAddDTO, space);
        String uid = RequestContextUtil.getUID();
        space.setUid(uid);
        if (enterpriseId != null) {
            Enterprise enterprise = enterpriseService.getEnterpriseById(enterpriseId);
            space.setEnterpriseId(enterpriseId);
            space.setType(Objects.equals(enterprise.getServiceType(), EnterpriseServiceTypeEnum.ENTERPRISE.getCode())
                    ? SpaceTypeEnum.ENTERPRISE.getCode()
                    : SpaceTypeEnum.TEAM.getCode());
            Long count = spaceService.countByEnterpriseId(enterpriseId);
            Integer spaceCount = 0;
            if (spaceLimitProperties != null) {
                if (Objects.equals(enterprise.getServiceType(), EnterpriseServiceTypeEnum.ENTERPRISE.getCode())) {
                    spaceCount = spaceLimitProperties.getEnterprise().getSpaceCount();
                } else if (Objects.equals(enterprise.getServiceType(), EnterpriseServiceTypeEnum.TEAM.getCode())) {
                    spaceCount = spaceLimitProperties.getTeam().getSpaceCount();
                }
            }
            if (spaceLimitProperties != null && count >= spaceCount) {
                return ApiResult.error(ResponseEnum.SPACE_ENTERPRISE_TEAM_MAX_EXCEEDED);
            }
        } else {
            Long count = spaceService.countByUid(uid);
            if (OrderInfoUtil.existValidProOrder(uid)) {
                space.setType(SpaceTypeEnum.PRO.getCode());
                if (spaceLimitProperties != null && count >= spaceLimitProperties.getPro().getSpaceCount()) {
                    return ApiResult.error(ResponseEnum.SPACE_PERSONAL_PRO_MAX_EXCEEDED);
                }
            } else {
                space.setType(SpaceTypeEnum.FREE.getCode());
                if (spaceLimitProperties != null && count >= spaceLimitProperties.getFree().getSpaceCount()) {
                    return ApiResult.error(ResponseEnum.SPACE_FREE_USER_MAX_EXCEEDED);
                }
            }
        }
        if (spaceService.save(space)) {
            if (!spaceUserService.addSpaceUser(space.getId(), space.getUid(), SpaceRoleEnum.OWNER)) {
                throw new BusinessException(ResponseEnum.INVITE_ADD_SPACE_USER_FAILED);
            }
            return ApiResult.success(space.getId());
        } else {
            return ApiResult.error(ResponseEnum.ENTERPRISE_CREATE_FAILED);
        }
    }

    @Override
    @Transactional
    public ApiResult<String> deleteSpace(Long spaceId, String mobile, String verifyCode) {
        Space space = spaceService.getById(spaceId);
        if (space == null) {
            return ApiResult.error(ResponseEnum.SPACE_NOT_EXISTS);
        }
        if (spaceService.removeById(spaceId)) {
            return ApiResult.success();
        } else {
            return ApiResult.error(ResponseEnum.SPACE_DELETE_FAILED);
        }
    }

    @Override
    @Transactional
    public ApiResult<String> updateSpace(SpaceUpdateDTO spaceUpdateDTO) {
        if (!Objects.equals(SpaceInfoUtil.getSpaceId(), spaceUpdateDTO.getId())) {
            return ApiResult.error(ResponseEnum.SPACE_APPLICATION_CURRENT_SPACE_INCONSISTENT);
        }
        Space space = spaceService.getById(spaceUpdateDTO.getId());
        if (spaceService.checkExistByName(spaceUpdateDTO.getName(), spaceUpdateDTO.getId())) {
            return ApiResult.error(ResponseEnum.SPACE_NAME_DUPLICATE);
        }
        space.setName(spaceUpdateDTO.getName());
        space.setDescription(spaceUpdateDTO.getDescription());
        space.setAvatarUrl(spaceUpdateDTO.getAvatarUrl());
        if (spaceService.updateById(space)) {
            return ApiResult.success();
        } else {
            return ApiResult.error(ResponseEnum.ENTERPRISE_UPDATE_FAILED);
        }
    }

    @Override
    @Transactional
    public ApiResult<Space> visitSpace(Long spaceId) {
        if (spaceId == null || spaceId <= 0L) {
            enterpriseService.setLastVisitEnterpriseId(null);
            spaceService.setLastVisitPersonalSpaceTime();
            return ApiResult.success();
        }
        Space space = spaceService.getById(spaceId);
        if (space == null) {
            return ApiResult.error(ResponseEnum.SPACE_NOT_EXISTS);
        }
        String uid = RequestContextUtil.getUID();
        SpaceUser spaceUser = spaceUserService.getSpaceUserByUid(spaceId, uid);
        if (spaceUser == null) {
            return ApiResult.error(ResponseEnum.SPACE_USER_NOT_IN_SPACE);
        }
        if (spaceUserService.updateVisitTime(spaceId, spaceUser.getUid())) {
            if (space.getEnterpriseId() != null) {
                enterpriseService.setLastVisitEnterpriseId(space.getEnterpriseId());
            }
            return ApiResult.success(space);
        } else {
            return ApiResult.error(ResponseEnum.ENTERPRISE_UPDATE_FAILED);
        }
    }

    @Override
    public ApiResult<String> sendMessageCode(Long spaceId) {
        Space space = spaceService.getById(spaceId);
        if (space == null) {
            return ApiResult.error(ResponseEnum.SPACE_NOT_EXISTS);
        }
        String uid = RequestContextUtil.getUID();
        SpaceUser spaceUser = spaceUserService.getSpaceUserByUid(spaceId, uid);
        if (spaceUser == null) {
            return ApiResult.error(ResponseEnum.SPACE_USER_NOT_IN_SPACE);
        }
        if (space.getEnterpriseId() == null && !Objects.equals(spaceUser.getRole(), SpaceRoleEnum.OWNER.getCode())) {
            return ApiResult.error(ResponseEnum.SPACE_USER_NOT_OWNER);
        }
        if (space.getEnterpriseId() != null) {
            EnterpriseUser enterpriseUser = enterpriseUserService.getEnterpriseUserByUid(space.getEnterpriseId(), uid);
            if (enterpriseUser == null) {
                return ApiResult.error(ResponseEnum.SPACE_USER_NOT_ENTERPRISE_USER);
            }
            if (!(Objects.equals(enterpriseUser.getRole(), EnterpriseRoleEnum.OFFICER.getCode()) ||
                    Objects.equals(enterpriseUser.getRole(), EnterpriseRoleEnum.GOVERNOR.getCode()))) {
                return ApiResult.error(ResponseEnum.SPACE_USER_NOT_ENTERPRISE_ADMIN);
            }
        }
        return ApiResult.success();
    }

    @Override
    public ApiResult<Boolean> ossVersionUserUpgrade() {
        String uid = RequestContextUtil.getUID();
        return ApiResult.success(userInfoDataService.updateUserEnterpriseServiceType(uid, EnterpriseServiceTypeEnum.ENTERPRISE));
    }

}
