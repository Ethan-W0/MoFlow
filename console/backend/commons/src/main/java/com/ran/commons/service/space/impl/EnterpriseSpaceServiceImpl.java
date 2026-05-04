package com.ran.commons.service.space.impl;

import com.ran.commons.entity.space.EnterprisePermission;
import com.ran.commons.entity.space.EnterpriseUser;
import com.ran.commons.entity.space.SpacePermission;
import com.ran.commons.entity.space.SpaceUser;
import com.ran.commons.service.space.EnterpriseSpaceService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Cacheable(value = "space:space_payer",key = "#spaceId",unless = "#result == null ",cacheManager = "cacheManager10s")
public class EnterpriseSpaceServiceImpl implements EnterpriseSpaceService {
    @Override
    public String getUidByCurrentSpaceId(Long spaceId) {
        if (spaceId == null) {
            return null;
        }

    }

    @Override
    public SpaceUser checkUserBelongSpace(Long spaceId, String uid) {
        return null;
    }

    @Override
    public void clearSpaceUserCache(Long spaceId, String uid) {

    }

    @Override
    public void clearEnterpriseUserCache(Long enterpriseId, String uid) {

    }

    @Override
    public boolean checkEnterpriseExpired(Long enterpriseId) {
        return false;
    }

    @Override
    public boolean checkSpaceExpired(Long spaceId) {
        return false;
    }

    @Override
    public SpacePermission getSpacePermissionByKey(String key) {
        return null;
    }

    @Override
    public EnterprisePermission getEnterprisePermissionByKey(String key) {
        return null;
    }

    @Override
    public EnterpriseUser checkUserBelongEnterprise(Long enterpriseId, String uid) {
        return null;
    }
}
