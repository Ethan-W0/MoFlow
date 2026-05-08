package com.ran.hub.service.space.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.ran.commons.constant.ResponseEnum;
import com.ran.commons.data.UserInfoDataService;
import com.ran.commons.entity.space.EnterpriseUser;
import com.ran.commons.entity.space.Enterprise;
import com.ran.commons.entity.space.InviteRecord;
import com.ran.commons.entity.space.Space;
import com.ran.commons.entity.space.SpaceUser;
import com.ran.commons.entity.user.UserInfo;
import com.ran.commons.enums.space.EnterpriseRoleEnum;
import com.ran.commons.enums.space.EnterpriseServiceTypeEnum;
import com.ran.commons.enums.space.InviteRecordRoleEnum;
import com.ran.commons.enums.space.InviteRecordStatusEnum;
import com.ran.commons.enums.space.InviteRecordTypeEnum;
import com.ran.commons.enums.space.SpaceRoleEnum;
import com.ran.commons.enums.space.SpaceTypeEnum;
import com.ran.commons.exception.BusinessException;
import com.ran.commons.response.ApiResult;
import com.ran.commons.service.space.EnterpriseService;
import com.ran.commons.service.space.EnterpriseUserService;
import com.ran.commons.service.space.InviteRecordService;
import com.ran.commons.service.space.SpaceService;
import com.ran.commons.service.space.SpaceUserService;
import com.ran.commons.util.RequestContextUtil;
import com.ran.commons.dto.space.InviteRecordAddDTO;
import com.ran.commons.dto.space.BatchChatUserVO;
import com.ran.commons.dto.space.ChatUserVO;
import com.ran.commons.dto.space.InviteRecordVO;
import com.ran.commons.dto.space.UserLimitVO;
import com.ran.commons.util.space.EnterpriseInfoUtil;
import com.ran.commons.util.space.SpaceInfoUtil;
import com.ran.hub.properties.SpaceLimitProperties;
import com.ran.hub.service.space.InviteRecordBizService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InviteRecordBizServiceImpl implements InviteRecordBizService {
    private static final int MAX_EXPIRE_TIME = 7;

    @Autowired
    private SpaceUserService spaceUserService;
    @Autowired
    private EnterpriseUserService enterpriseUserService;
    @Autowired
    private SpaceService spaceService;
    @Autowired
    private EnterpriseService enterpriseService;
    @Autowired(required = false)
    private SpaceLimitProperties spaceLimitProperties;
    @Autowired
    private InviteRecordService inviteRecordService;
    @Autowired
    private UserInfoDataService userInfoDataService;

    @Override
    @Transactional
    public ApiResult<String> spaceInvite(List<InviteRecordAddDTO> dtos) {
        List<String> uids = dtos.stream().map(InviteRecordAddDTO::getUid).collect(Collectors.toList());
        Long spaceId = SpaceInfoUtil.getSpaceId();
        Space space = spaceService.getSpaceById(spaceId);
        if (spaceLimitProperties != null) {
            if (Objects.equals(space.getType(), SpaceTypeEnum.FREE.getCode())) {
                if ((spaceUserService.countFreeSpaceUser(space.getUid())
                        + inviteRecordService.countJoiningByUid(space.getUid(), SpaceTypeEnum.FREE) + dtos.size()) > spaceLimitProperties.getFree().getUserCount()) {
                    return ApiResult.error(ResponseEnum.INVITE_SPACE_USER_FULL);
                }
            } else if (Objects.equals(space.getType(), SpaceTypeEnum.PRO.getCode())) {
                if ((spaceUserService.countProSpaceUser(space.getUid())
                        + inviteRecordService.countJoiningByUid(space.getUid(), SpaceTypeEnum.PRO) + dtos.size()) > spaceLimitProperties.getPro().getUserCount()) {
                    return ApiResult.error(ResponseEnum.INVITE_SPACE_USER_FULL);
                }
            } else if (Objects.equals(space.getType(), SpaceTypeEnum.TEAM.getCode())) {
                if ((enterpriseUserService.countByEnterpriseId(space.getEnterpriseId())
                        + inviteRecordService.countJoiningByEnterpriseId(space.getEnterpriseId()) + dtos.size()) > spaceLimitProperties.getTeam().getUserCount()) {
                    return ApiResult.error(ResponseEnum.INVITE_TEAM_USER_FULL);
                }
            } else if (Objects.equals(space.getType(), SpaceTypeEnum.ENTERPRISE.getCode())) {
                if ((enterpriseUserService.countByEnterpriseId(space.getEnterpriseId())
                        + inviteRecordService.countJoiningByEnterpriseId(space.getEnterpriseId()) + dtos.size()) > spaceLimitProperties.getEnterprise().getUserCount()) {
                    return ApiResult.error(ResponseEnum.INVITE_ENTERPRISE_USER_FULL);
                }
            }
        }
        Long count = spaceUserService.countSpaceUserByUids(spaceId, uids);
        if (count > 0) {
            return ApiResult.error(ResponseEnum.INVITE_USER_ALREADY_SPACE_MEMBER);
        }
        if (inviteRecordService.countBySpaceIdAndUids(spaceId, uids) > 0) {
            return ApiResult.error(ResponseEnum.INVITE_USER_ALREADY_INVITED);
        }
        List<InviteRecord> inviteRecords = new ArrayList<>();
        String uid = RequestContextUtil.getUID();
        for (InviteRecordAddDTO dto : dtos) {
            InviteRecord inviteRecord = new InviteRecord();
            inviteRecord.setType(InviteRecordTypeEnum.SPACE.getCode());
            inviteRecord.setSpaceId(spaceId);
            inviteRecord.setEnterpriseId(space.getEnterpriseId());
            inviteRecord.setInviteeUid(dto.getUid());
            inviteRecord.setRole(dto.getRole());
            UserInfo userInfo = userInfoDataService.findByUid(dto.getUid()).orElseThrow();
            inviteRecord.setInviteeNickname(userInfo.getNickname());
            inviteRecord.setInviterUid(uid);
            inviteRecord.setStatus(InviteRecordStatusEnum.INIT.getCode());
            inviteRecord.setExpireTime(LocalDateTime.now().plusDays(MAX_EXPIRE_TIME));
            inviteRecords.add(inviteRecord);
        }
        if (inviteRecordService.saveBatch(inviteRecords)) {
            return ApiResult.success();
        } else {
            return ApiResult.error(ResponseEnum.INVITE_FAILED);
        }
    }

    @Override
    @Transactional
    public ApiResult<String> enterpriseInvite(List<InviteRecordAddDTO> dtos) {
        List<String> uids = dtos.stream().map(InviteRecordAddDTO::getUid).collect(Collectors.toList());
        Long enterpriseId = EnterpriseInfoUtil.getEnterpriseId();
        Enterprise enterprise = enterpriseService.getEnterpriseById(enterpriseId);
        Integer userCount = 0;
        if (spaceLimitProperties != null) {
            if (Objects.equals(enterprise.getServiceType(), EnterpriseServiceTypeEnum.ENTERPRISE.getCode())) {
                userCount = spaceLimitProperties.getEnterprise().getUserCount();
            } else if (Objects.equals(enterprise.getServiceType(), EnterpriseServiceTypeEnum.TEAM.getCode())) {
                userCount = spaceLimitProperties.getTeam().getUserCount();
            }
        }
        if (spaceLimitProperties != null && (enterpriseUserService.countByEnterpriseId(enterpriseId)
                + inviteRecordService.countJoiningByEnterpriseId(enterpriseId) + dtos.size()) > userCount) {
            return ApiResult.error(ResponseEnum.INVITE_ENTERPRISE_USER_FULL);
        }
        Long count = enterpriseUserService.countByEnterpriseIdAndUids(enterpriseId, uids);
        if (count > 0) {
            return ApiResult.error(ResponseEnum.INVITE_USER_ALREADY_TEAM_MEMBER);
        }
        if (inviteRecordService.countByEnterpriseIdAndUids(enterpriseId, uids) > 0) {
            return ApiResult.error(ResponseEnum.INVITE_USER_ALREADY_INVITED);
        }
        List<InviteRecord> inviteRecords = new ArrayList<>();
        String uid = RequestContextUtil.getUID();
        for (InviteRecordAddDTO dto : dtos) {
            InviteRecord inviteRecord = new InviteRecord();
            inviteRecord.setType(InviteRecordTypeEnum.ENTERPRISE.getCode());
            inviteRecord.setEnterpriseId(enterpriseId);
            inviteRecord.setInviteeUid(dto.getUid());
            inviteRecord.setRole(dto.getRole());
            UserInfo userInfo = userInfoDataService.findByUid(dto.getUid()).orElseThrow();
            inviteRecord.setInviteeNickname(userInfo.getNickname());
            inviteRecord.setInviterUid(uid);
            inviteRecord.setStatus(InviteRecordStatusEnum.INIT.getCode());
            inviteRecord.setExpireTime(LocalDateTime.now().plusDays(MAX_EXPIRE_TIME));
            inviteRecords.add(inviteRecord);
        }
        if (inviteRecordService.saveBatch(inviteRecords)) {
            return ApiResult.success();
        } else {
            return ApiResult.error(ResponseEnum.INVITE_FAILED);
        }
    }

    @Override
    @Transactional
    public ApiResult<String> acceptInvite(Long inviteId) {
        InviteRecord inviteRecord = inviteRecordService.getById(inviteId);
        ApiResult<String> responseMsg = checkInviteRecord(inviteRecord);
        if (responseMsg != null) {
            return responseMsg;
        }
        inviteRecord.setStatus(InviteRecordStatusEnum.ACCEPT.getCode());
        if (!inviteRecordService.updateById(inviteRecord)) {
            return ApiResult.error(ResponseEnum.OPERATION_FAILED);
        }
        if (InviteRecordTypeEnum.ENTERPRISE.getCode().equals(inviteRecord.getType())) {
            if (!enterpriseUserService.addEnterpriseUser(inviteRecord.getEnterpriseId(), inviteRecord.getInviteeUid(),
                    Objects.equals(InviteRecordRoleEnum.ADMIN.getCode(), inviteRecord.getRole()) ? EnterpriseRoleEnum.GOVERNOR : EnterpriseRoleEnum.STAFF)) {
                throw new BusinessException(ResponseEnum.INVITE_ADD_TEAM_USER_FAILED);
            }
        } else if (InviteRecordTypeEnum.SPACE.getCode().equals(inviteRecord.getType())) {
            if (!spaceUserService.addSpaceUser(inviteRecord.getSpaceId(), inviteRecord.getInviteeUid(),
                    Objects.equals(InviteRecordRoleEnum.ADMIN.getCode(), inviteRecord.getRole()) ? SpaceRoleEnum.ADMIN : SpaceRoleEnum.MEMBER)) {
                throw new BusinessException(ResponseEnum.SPACE_USER_ADD_FAILED);
            }
            Space space = spaceService.getSpaceById(inviteRecord.getSpaceId());
            if (space.getEnterpriseId() != null) {
                if (!enterpriseUserService.addEnterpriseUser(space.getEnterpriseId(), inviteRecord.getInviteeUid(),
                        Objects.equals(InviteRecordRoleEnum.ADMIN.getCode(), inviteRecord.getRole()) ? EnterpriseRoleEnum.GOVERNOR : EnterpriseRoleEnum.STAFF)) {
                    throw new BusinessException(ResponseEnum.INVITE_ADD_TEAM_USER_FAILED);
                }
            }
        } else {
            throw new BusinessException(ResponseEnum.INVITE_UNSUPPORTED_TYPE);
        }
        return ApiResult.success();
    }

    @Override
    @Transactional
    public ApiResult<String> refuseInvite(Long inviteId) {
        InviteRecord inviteRecord = inviteRecordService.getById(inviteId);
        ApiResult<String> responseMsg = checkInviteRecord(inviteRecord);
        if (responseMsg != null) {
            return responseMsg;
        }
        inviteRecord.setStatus(InviteRecordStatusEnum.REFUSE.getCode());
        if (inviteRecordService.updateById(inviteRecord)) {
            return ApiResult.success();
        } else {
            return ApiResult.error(ResponseEnum.OPERATION_FAILED);
        }
    }

    private ApiResult<String> checkInviteRecord(InviteRecord inviteRecord) {
        if (inviteRecord == null) {
            return ApiResult.error(ResponseEnum.INVITE_RECORD_NOT_FOUND);
        }
        if (!Objects.equals(inviteRecord.getInviteeUid(), RequestContextUtil.getUID())) {
            return ApiResult.error(ResponseEnum.INVITE_CURRENT_USER_NOT_INVITEE);
        }
        if (Objects.equals(inviteRecord.getStatus(), InviteRecordStatusEnum.REFUSE.getCode())) {
            return ApiResult.error(ResponseEnum.INVITE_ALREADY_REFUSED);
        }
        if (Objects.equals(inviteRecord.getStatus(), InviteRecordStatusEnum.ACCEPT.getCode())) {
            return ApiResult.error(ResponseEnum.INVITE_ALREADY_ACCEPTED);
        }
        if (Objects.equals(inviteRecord.getStatus(), InviteRecordStatusEnum.WITHDRAW.getCode())) {
            return ApiResult.error(ResponseEnum.INVITE_ALREADY_WITHDRAWN);
        }
        if (Objects.equals(inviteRecord.getStatus(), InviteRecordStatusEnum.EXPIRED.getCode())) {
            return ApiResult.error(ResponseEnum.INVITE_ALREADY_EXPIRED);
        }
        if (inviteRecord.getExpireTime().isBefore(LocalDateTime.now())) {
            return ApiResult.error(ResponseEnum.INVITE_ALREADY_EXPIRED);
        }
        return null;
    }

    @Override
    @Transactional
    public ApiResult<String> revokeEnterpriseInvite(Long inviteId) {
        InviteRecord inviteRecord = inviteRecordService.getById(inviteId);
        if (inviteRecord == null) {
            return ApiResult.error(ResponseEnum.INVITE_RECORD_NOT_FOUND);
        }
        if (!Objects.equals(inviteRecord.getEnterpriseId(), EnterpriseInfoUtil.getEnterpriseId())) {
            return ApiResult.error(ResponseEnum.INVITE_ENTERPRISE_INCONSISTENT);
        }
        if (!Objects.equals(inviteRecord.getStatus(), InviteRecordStatusEnum.INIT.getCode())) {
            return ApiResult.error(ResponseEnum.INVITE_STATUS_NOT_SUPPORTED);
        }
        inviteRecord.setStatus(InviteRecordStatusEnum.WITHDRAW.getCode());
        if (inviteRecordService.updateById(inviteRecord)) {
            return ApiResult.success();
        } else {
            return ApiResult.error(ResponseEnum.OPERATION_FAILED);
        }
    }

    @Override
    @Transactional
    public ApiResult<String> revokeSpaceInvite(Long inviteId) {
        InviteRecord inviteRecord = inviteRecordService.getById(inviteId);
        if (inviteRecord == null) {
            return ApiResult.error(ResponseEnum.INVITE_RECORD_NOT_FOUND);
        }
        if (!Objects.equals(inviteRecord.getSpaceId(), SpaceInfoUtil.getSpaceId())) {
            return ApiResult.error(ResponseEnum.SPACE_APPLICATION_CURRENT_SPACE_INCONSISTENT);
        }
        if (!Objects.equals(inviteRecord.getStatus(), InviteRecordStatusEnum.INIT.getCode())) {
            return ApiResult.error(ResponseEnum.INVITE_STATUS_NOT_SUPPORTED);
        }
        inviteRecord.setStatus(InviteRecordStatusEnum.WITHDRAW.getCode());
        if (inviteRecordService.updateById(inviteRecord)) {
            return ApiResult.success();
        } else {
            return ApiResult.error(ResponseEnum.OPERATION_FAILED);
        }
    }

    @Override
    public InviteRecordVO getRecordByParam(String param) {
        long id;
        try {
            id = Long.parseLong(param);
        } catch (Exception e) {
            log.error("Failed to parse invitation parameters", e);
            throw new BusinessException(ResponseEnum.INVITE_PARAMETER_EXCEPTION);
        }
        InviteRecordVO vo = inviteRecordService.selectVOById(id);
        if (vo == null) {
            throw new BusinessException(ResponseEnum.INVITE_RECORD_NOT_FOUND);
        }
        UserInfo inviterUser = userInfoDataService.findByUid(vo.getInviterUid()).orElseThrow();
        vo.setInviterName(inviterUser.getNickname());
        vo.setInviterAvatar(inviterUser.getAvatar());
        if (Objects.equals(InviteRecordTypeEnum.SPACE.getCode(), vo.getType())) {
            SpaceUser spaceOwner = spaceUserService.getSpaceOwner(vo.getSpaceId());
            if (spaceOwner != null) {
                UserInfo ownerUser = userInfoDataService.findByUid(spaceOwner.getUid()).orElseThrow();
                vo.setOwnerName(ownerUser.getNickname());
                vo.setOwnerAvatar(ownerUser.getAvatar());
            }
            Space space = spaceService.getSpaceById(vo.getSpaceId());
            if (space != null) {
                vo.setSpaceName(space.getName());
                vo.setSpaceAvatar(space.getAvatarUrl());
                vo.setSpaceDescription(space.getDescription());
                vo.setIsBelong(spaceUserService.getSpaceUserByUid(vo.getSpaceId(), vo.getInviteeUid()) != null);
            } else {
                throw new BusinessException(ResponseEnum.INVITE_SPACE_ALREADY_DELETED);
            }
        } else if (Objects.equals(InviteRecordTypeEnum.ENTERPRISE.getCode(), vo.getType())) {
            Enterprise enterprise = enterpriseService.getEnterpriseById(vo.getEnterpriseId());
            vo.setEnterpriseName(enterprise.getName());
            vo.setEnterpriseAvatar(enterprise.getAvatarUrl());
            UserInfo ownerUser = userInfoDataService.findByUid(enterprise.getUid()).orElseThrow();
            vo.setOwnerName(ownerUser.getNickname());
            vo.setOwnerAvatar(ownerUser.getAvatar());
            vo.setIsBelong(enterpriseUserService.getEnterpriseUserByUid(vo.getEnterpriseId(), vo.getInviteeUid()) != null);
        }
        return vo;
    }

    private Set<String> getJoinedUids(InviteRecordTypeEnum type) {
        if (type == InviteRecordTypeEnum.SPACE) {
            Long spaceId = SpaceInfoUtil.getSpaceId();
            List<SpaceUser> allSpaceUsers = spaceUserService.getAllSpaceUsers(spaceId);
            return allSpaceUsers.stream().map(SpaceUser::getUid).collect(Collectors.toSet());
        } else {
            Long enterpriseId = EnterpriseInfoUtil.getEnterpriseId();
            List<EnterpriseUser> enterpriseUsers = enterpriseUserService.listByEnterpriseId(enterpriseId);
            return enterpriseUsers.stream().map(EnterpriseUser::getUid).collect(Collectors.toSet());
        }
    }

    @Override
    public List<ChatUserVO> searchUser(String mobile, InviteRecordTypeEnum type) {
        List<UserInfo> userInfos = userInfoDataService.findUsersByMobile(mobile);
        return getChatUserVOS(type, userInfos);
    }

    @Override
    public List<ChatUserVO> searchUsername(String username, InviteRecordTypeEnum type) {
        List<UserInfo> userInfos = userInfoDataService.findUsersByUsername(username);
        return getChatUserVOS(type, userInfos);
    }

    private List<ChatUserVO> getChatUserVOS(InviteRecordTypeEnum type, List<UserInfo> userInfos) {
        if (CollectionUtil.isNotEmpty(userInfos)) {
            Set<String> joinedUids = getJoinedUids(type);
            Set<String> invitingUids = inviteRecordService.getInvitingUids(type);
            Map<String, String> mobileMap = userInfos.stream()
                    .filter(i -> i.getUid() != null)
                    .collect(Collectors.toMap(UserInfo::getUid, i -> i.getMobile() != null ? i.getMobile() : ""));
            return userInfos.stream().map(i -> {
                ChatUserVO chatUserVO = new ChatUserVO();
                chatUserVO.setMobile(mobileMap.get(i.getUid()));
                chatUserVO.setUsername(i.getUsername());
                chatUserVO.setNickname(i.getNickname());
                chatUserVO.setUid(i.getUid());
                chatUserVO.setAvatar(i.getAvatar());
                if (joinedUids.contains(i.getUid())) {
                    chatUserVO.setStatus(1);
                } else if (invitingUids.contains(i.getUid())) {
                    chatUserVO.setStatus(2);
                } else {
                    chatUserVO.setStatus(0);
                }
                return chatUserVO;
            }).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public ApiResult<BatchChatUserVO> searchUserBatch(MultipartFile file) {
        return ApiResult.error(ResponseEnum.OPERATION_FAILED);
    }

    @Override
    public ApiResult<BatchChatUserVO> searchUsernameBatch(MultipartFile file) {
        return ApiResult.error(ResponseEnum.OPERATION_FAILED);
    }

}
