package com.ran.commons.service.space.impl;

import com.ran.commons.entity.space.Enterprise;
import com.ran.commons.entity.space.EnterprisePermission;
import com.ran.commons.entity.space.EnterpriseUser;
import com.ran.commons.entity.space.Space;
import com.ran.commons.entity.space.SpacePermission;
import com.ran.commons.entity.space.SpaceUser;
import com.ran.commons.service.space.EnterpriseService;
import com.ran.commons.service.space.EnterpriseSpaceService;
import com.ran.commons.service.space.EnterpriseUserService;
import com.ran.commons.service.space.SpaceService;
import com.ran.commons.service.space.SpaceUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnterpriseSpaceServiceImpl implements EnterpriseSpaceService {
    @Autowired
    private SpaceUserService spaceUserService;
    @Autowired
    private EnterpriseService enterpriseService;
    @Autowired
    private SpaceService spaceService;
    @Autowired
    private EnterpriseUserService enterpriseUserService;

    @Override
    @Transactional
    @Cacheable(value = "space:space_payer", key = "#spaceId", unless = "#result == null", cacheManager = "cacheManager10s")
    public String getUidByCurrentSpaceId(Long spaceId) {
        if (spaceId == null) {
            return null;
        }
        Space space = spaceService.getSpaceById(spaceId);
        if (space == null) {
            return null;
        }
        if (space.getEnterpriseId() == null) {
            SpaceUser owner = spaceUserService.getSpaceOwner(spaceId);
            return owner == null ? null : owner.getUid();
        }
        Enterprise enterprise = enterpriseService.getEnterpriseById(space.getEnterpriseId());
        return enterprise == null ? null : enterprise.getUid();
    }

    @Override
    @Cacheable(value = "space:space_user", key = "#spaceId + '_' + #uid", unless = "#result == null", cacheManager = "cacheManager10s")
    public SpaceUser checkUserBelongSpace(Long spaceId, String uid) {
        return spaceUserService.getSpaceUserByUid(spaceId, uid);
    }

    @Override
    @CacheEvict(value = "space:space_user", key = "#spaceId + '_' + #uid", cacheManager = "cacheManager10s")
    public void clearSpaceUserCache(Long spaceId, String uid) {
    }

    @Override
    @Cacheable(value = "space:enterprise_user", key = "#enterpriseId + '_' + #uid", unless = "#result == null", cacheManager = "cacheManager10s")
    public EnterpriseUser checkUserBelongEnterprise(Long enterpriseId, String uid) {
        return enterpriseUserService.getEnterpriseUserByUid(enterpriseId, uid);
    }

    @Override
    @CacheEvict(value = "space:enterprise_user", key = "#enterpriseId + '_' + #uid", cacheManager = "cacheManager10s")
    public void clearEnterpriseUserCache(Long enterpriseId, String uid) {
    }

    @Override
    @Cacheable(value = "space:enterprise_expired", key = "#enterpriseId", cacheManager = "cacheManager10s")
    @Transactional
    public boolean checkEnterpriseExpired(Long enterpriseId) {
        Enterprise enterprise = enterpriseService.getEnterpriseById(enterpriseId);
        if (enterprise == null) {
            return true;
        }
        return enterprise.getExpireTime().isBefore(java.time.LocalDateTime.now());
    }

    @Override
    @Cacheable(value = "space:space_expired", key = "#spaceId", cacheManager = "cacheManager10s")
    public boolean checkSpaceExpired(Long spaceId) {
        Space space = spaceService.getSpaceById(spaceId);
        if (space == null) {
            return true;
        }
        if (space.getEnterpriseId() != null) {
            return this.checkEnterpriseExpired(space.getEnterpriseId());
        }
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
}
