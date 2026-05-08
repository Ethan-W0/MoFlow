package com.ran.hub.service.space.impl;

import com.ran.commons.constant.ResponseEnum;
import com.ran.commons.data.UserInfoDataService;
import com.ran.commons.entity.user.UserInfo;
import com.ran.commons.exception.BusinessException;
import com.ran.commons.response.ApiResult;
import com.ran.commons.util.RequestContextUtil;
import com.ran.commons.entity.space.ApplyRecord;
import com.ran.commons.entity.space.EnterpriseUser;
import com.ran.commons.entity.space.SpaceUser;
import com.ran.commons.enums.space.EnterpriseRoleEnum;
import com.ran.commons.enums.space.SpaceRoleEnum;
import com.ran.commons.service.space.ApplyRecordService;
import com.ran.commons.service.space.EnterpriseUserService;
import com.ran.commons.service.space.SpaceUserService;
import com.ran.hub.service.space.ApplyRecordBizService;
import com.ran.commons.util.space.EnterpriseInfoUtil;
import com.ran.commons.util.space.SpaceInfoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Slf4j
public class ApplyRecordBizServiceImpl implements ApplyRecordBizService {

    @Autowired
    private SpaceUserService spaceUserService;
    @Autowired
    private UserInfoDataService userInfoDataService;
    @Autowired
    private EnterpriseUserService enterpriseUserService;
    @Autowired
    private ApplyRecordService applyRecordService;

    @Override
    @Transactional
    public ApiResult<String> joinEnterpriseSpace(Long spaceId) {
        String uid = RequestContextUtil.getUID();
        Long enterpriseId = EnterpriseInfoUtil.getEnterpriseId();
        if (enterpriseId == null) {
            return ApiResult.error(ResponseEnum.SPACE_APPLICATION_PLEASE_JOIN_ENTERPRISE_FIRST);
        }
        if (applyRecordService.getByUidAndSpaceId(uid, spaceId) != null) {
            return ApiResult.error(ResponseEnum.SPACE_APPLICATION_DUPLICATE_NOT_ALLOWED);
        }
        SpaceUser spaceUser = spaceUserService.getSpaceUserByUid(spaceId, uid);
        if (spaceUser != null) {
            return ApiResult.error(ResponseEnum.SPACE_APPLICATION_USER_ALREADY_IN_SPACE);
        }
        EnterpriseUser enterpriseUser = enterpriseUserService.getEnterpriseUserByUid(enterpriseId, uid);
        if (Objects.equals(enterpriseUser.getRole(), EnterpriseRoleEnum.OFFICER.getCode())) {
            if (spaceUserService.addSpaceUser(spaceId, uid, SpaceRoleEnum.ADMIN)) {
                return ApiResult.success();
            } else {
                return ApiResult.error(ResponseEnum.SPACE_APPLICATION_JOIN_FAILED);
            }
        } else {
            ApplyRecord applyRecord = new ApplyRecord();
            applyRecord.setEnterpriseId(enterpriseId);
            applyRecord.setSpaceId(spaceId);
            applyRecord.setApplyUid(uid);
            UserInfo userInfo = userInfoDataService.findByUid(uid).orElseThrow();
            applyRecord.setApplyNickname(userInfo.getNickname());
            applyRecord.setApplyTime(LocalDateTime.now());
            applyRecord.setStatus(ApplyRecord.Status.APPLYING.getCode());
            if (applyRecordService.save(applyRecord)) {
                return ApiResult.success();
            } else {
                return ApiResult.error(ResponseEnum.SPACE_APPLICATION_FAILED);
            }
        }
    }

    @Override
    @Transactional
    public ApiResult<String> agreeEnterpriseSpace(Long applyId) {
        ApplyRecord applyRecord = applyRecordService.getById(applyId);
        if (applyRecord == null) {
            return ApiResult.error(ResponseEnum.SPACE_APPLICATION_RECORD_NOT_FOUND);
        }
        if (!Objects.equals(applyRecord.getSpaceId(), SpaceInfoUtil.getSpaceId())) {
            return ApiResult.error(ResponseEnum.SPACE_APPLICATION_CURRENT_SPACE_INCONSISTENT);
        }
        if (!Objects.equals(applyRecord.getStatus(), ApplyRecord.Status.APPLYING.getCode())) {
            return ApiResult.error(ResponseEnum.SPACE_APPLICATION_STATUS_INCORRECT);
        }
        applyRecord.setStatus(ApplyRecord.Status.APPROVED.getCode());
        applyRecord.setAuditTime(LocalDateTime.now());
        applyRecord.setAuditUid(RequestContextUtil.getUID());
        if (!applyRecordService.updateById(applyRecord)) {
            return ApiResult.error(ResponseEnum.SPACE_APPLICATION_APPROVAL_FAILED);
        }
        if (!spaceUserService.addSpaceUser(applyRecord.getSpaceId(), applyRecord.getApplyUid(), SpaceRoleEnum.MEMBER)) {
            throw new BusinessException(ResponseEnum.SPACE_USER_ADD_FAILED);
        }
        return ApiResult.success();
    }

    @Override
    @Transactional
    public ApiResult<String> refuseEnterpriseSpace(Long applyId) {
        ApplyRecord applyRecord = applyRecordService.getById(applyId);
        if (applyRecord == null) {
            return ApiResult.error(ResponseEnum.SPACE_APPLICATION_RECORD_NOT_FOUND);
        }
        if (!Objects.equals(applyRecord.getSpaceId(), SpaceInfoUtil.getSpaceId())) {
            return ApiResult.error(ResponseEnum.SPACE_APPLICATION_CURRENT_SPACE_INCONSISTENT);
        }
        if (!Objects.equals(applyRecord.getStatus(), ApplyRecord.Status.APPLYING.getCode())) {
            return ApiResult.error(ResponseEnum.SPACE_APPLICATION_STATUS_INCORRECT);
        }
        applyRecord.setStatus(ApplyRecord.Status.REJECTED.getCode());
        applyRecord.setAuditTime(LocalDateTime.now());
        applyRecord.setAuditUid(RequestContextUtil.getUID());
        if (applyRecordService.updateById(applyRecord)) {
            return ApiResult.success();
        } else {
            return ApiResult.error(ResponseEnum.SPACE_APPLICATION_APPROVAL_FAILED);
        }
    }
}
