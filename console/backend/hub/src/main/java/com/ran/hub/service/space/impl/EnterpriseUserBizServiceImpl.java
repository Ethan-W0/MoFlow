package com.ran.hub.service.space.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.ran.commons.constant.ResponseEnum;
import com.ran.commons.response.ApiResult;
import com.ran.commons.service.space.EnterpriseService;
import com.ran.commons.service.space.EnterpriseSpaceService;
import com.ran.commons.service.space.EnterpriseUserService;
import com.ran.commons.service.space.InviteRecordService;
import com.ran.commons.service.space.SpaceService;
import com.ran.commons.service.space.SpaceUserService;
import com.ran.commons.util.RequestContextUtil;
import com.ran.commons.entity.space.Enterprise;
import com.ran.commons.entity.space.EnterpriseUser;
import com.ran.commons.enums.space.EnterpriseRoleEnum;
import com.ran.commons.enums.space.EnterpriseServiceTypeEnum;
import com.ran.commons.enums.space.SpaceRoleEnum;
import com.ran.hub.properties.SpaceLimitProperties;
import com.ran.hub.service.space.EnterpriseUserBizService;
import com.ran.commons.util.space.EnterpriseInfoUtil;
import com.ran.commons.dto.space.SpaceVO;
import com.ran.commons.dto.space.UserLimitVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EnterpriseUserBizServiceImpl implements EnterpriseUserBizService {
    @Autowired
    private SpaceUserService spaceUserService;
    @Autowired
    private SpaceService spaceService;
    @Autowired
    private EnterpriseService enterpriseService;
    @Autowired(required = false)
    private SpaceLimitProperties spaceLimitProperties;
    @Autowired
    private InviteRecordService inviteRecordService;
    @Autowired
    private EnterpriseSpaceService enterpriseSpaceService;
    @Autowired
    private EnterpriseUserService enterpriseUserService;

    @Override
    @Transactional
    public ApiResult<String> remove(String uid) {
        Long enterpriseId = EnterpriseInfoUtil.getEnterpriseId();
        EnterpriseUser enterpriseUser = enterpriseUserService.getEnterpriseUserByUid(enterpriseId, uid);
        if (enterpriseUser == null) {
            return ApiResult.error(ResponseEnum.ENTERPRISE_TEAM_USER_NOT_IN_TEAM);
        }
        if (Objects.equals(enterpriseUser.getRole(), EnterpriseRoleEnum.OFFICER.getCode())) {
            return ApiResult.error(ResponseEnum.ENTERPRISE_TEAM_SUPER_ADMIN_CANNOT_BE_REMOVED);
        }
        if (!removeEnterpriseUser(enterpriseUser)) {
            return ApiResult.error(ResponseEnum.ENTERPRISE_TEAM_REMOVE_USER_FAILED);
        }
        enterpriseSpaceService.clearEnterpriseUserCache(enterpriseId, uid);
        return ApiResult.success();
    }

    @Override
    @Transactional
    public ApiResult<String> updateRole(String uid, Integer role) {
        Long enterpriseId = EnterpriseInfoUtil.getEnterpriseId();
        if (enterpriseId == null) {
            return ApiResult.error(ResponseEnum.SPACE_APPLICATION_PLEASE_JOIN_ENTERPRISE_FIRST);
        }
        EnterpriseUser enterpriseUser = enterpriseUserService.getEnterpriseUserByUid(enterpriseId, uid);
        if (enterpriseUser == null) {
            return ApiResult.error(ResponseEnum.ENTERPRISE_TEAM_USER_NOT_IN_TEAM);
        }
        EnterpriseRoleEnum roleEnum = EnterpriseRoleEnum.getByCode(role);
        if (roleEnum == null) {
            return ApiResult.error(ResponseEnum.ENTERPRISE_TEAM_ROLE_TYPE_INCORRECT);
        }
        enterpriseUser.setRole(role);
        if (!enterpriseUserService.updateById(enterpriseUser)) {
            enterpriseSpaceService.clearEnterpriseUserCache(enterpriseId, uid);
            return ApiResult.error(ResponseEnum.ENTERPRISE_TEAM_UPDATE_ROLE_FAILED);
        }
        return ApiResult.success();
    }

    @Override
    @Transactional
    public ApiResult<String> quitEnterprise() {
        Long enterpriseId = EnterpriseInfoUtil.getEnterpriseId();
        String uid = RequestContextUtil.getUID();
        EnterpriseUser enterpriseUser = enterpriseUserService.getEnterpriseUserByUid(enterpriseId, uid);
        if (Objects.equals(enterpriseUser.getRole(), EnterpriseRoleEnum.OFFICER.getCode())) {
            return ApiResult.error(ResponseEnum.ENTERPRISE_TEAM_SUPER_ADMIN_CANNOT_LEAVE_TEAM);
        }
        if (!removeEnterpriseUser(enterpriseUser)) {
            enterpriseSpaceService.clearEnterpriseUserCache(enterpriseId, uid);
            return ApiResult.error(ResponseEnum.ENTERPRISE_TEAM_LEAVE_FAILED);
        }
        return ApiResult.success();
    }

    @Override
    public UserLimitVO getUserLimit(Long enterpriseId) {
        Enterprise enterprise = enterpriseService.getEnterpriseById(enterpriseId);
        Integer userCount = 0;
        if (spaceLimitProperties != null) {
            if (Objects.equals(enterprise.getServiceType(), EnterpriseServiceTypeEnum.ENTERPRISE.getCode())) {
                userCount = spaceLimitProperties.getEnterprise().getUserCount();
            } else if (Objects.equals(enterprise.getServiceType(), EnterpriseServiceTypeEnum.TEAM.getCode())) {
                userCount = spaceLimitProperties.getTeam().getUserCount();
            }
        }
        UserLimitVO vo = new UserLimitVO();
        vo.setTotal(userCount);
        long used = enterpriseUserService.countByEnterpriseId(enterpriseId)
                + inviteRecordService.countJoiningByEnterpriseId(enterpriseId);
        vo.setUsed(Long.valueOf(used).intValue());
        vo.setRemain(vo.getTotal() - vo.getUsed());
        return vo;
    }

    private boolean removeEnterpriseUser(EnterpriseUser enterpriseUser) {
        List<SpaceVO> spaceVOS = spaceService.listByEnterpriseIdAndUid(enterpriseUser.getEnterpriseId(),
                enterpriseUser.getUid());
        String uid = enterpriseService.getUidByEnterpriseId(enterpriseUser.getEnterpriseId());
        if (CollectionUtil.isNotEmpty(spaceVOS)) {
            for (SpaceVO spaceVO : spaceVOS) {
                if (Objects.equals(spaceVO.getUserRole(), SpaceRoleEnum.OWNER.getCode())) {
                    spaceUserService.addSpaceUser(spaceVO.getId(), uid, SpaceRoleEnum.OWNER);
                }
            }
            spaceUserService.removeByUid(spaceVOS.stream()
                    .map(SpaceVO::getId)
                    .collect(Collectors.toSet()), enterpriseUser.getUid());
        }
        return enterpriseUserService.removeById(enterpriseUser);
    }
}
