package com.ran.commons.service.space.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ran.commons.dto.space.EnterpriseVO;
import com.ran.commons.entity.space.Enterprise;
import com.ran.commons.mapper.EnterpriseMapper;
import com.ran.commons.service.space.EnterpriseService;
import com.ran.commons.util.RequestContextUtil;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EnterpriseServiceImpl extends ServiceImpl<EnterpriseMapper,Enterprise> implements EnterpriseService {
    private static final String USER_LAST_VISIT_ENTERPRISE_ID = "USER_LAST_VISIT_ENTERPRISE_ID:";
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public Long getLastVisitEnterpriseId() {
        String uid = RequestContextUtil.getUID();
        String key = USER_LAST_VISIT_ENTERPRISE_ID + uid;
        Object idObj = redissonClient.getBucket(key).get();
        if (idObj == null) {
            return null;
        }
        String idStr = idObj.toString();
        if (StringUtils.isNotBlank(idStr)) {
            return Long.valueOf(idStr);
        }
        return null;
    }

    @Override
    public boolean setLastVisitEnterpriseId(Long enterpriseId) {
        String uid = RequestContextUtil.getUID();
        String key = USER_LAST_VISIT_ENTERPRISE_ID + uid;
        if (enterpriseId == null) {
            return redissonClient.getBucket(key).delete();
        } else {
            redissonClient.getBucket(key).set(Long.toString(enterpriseId));
            return true;
        }
    }

    @Override
    public Integer checkNeedCreateTeam() {
        return 0;
    }

    @Override
    public void orderChangeNotify(String uid, LocalDateTime endTime) {

    }

    @Override
    public boolean checkCertification() {
        return false;
    }

    @Override
    public EnterpriseVO detail() {
        String uid = RequestContextUtil.getUID();

    }

    @Override
    public List<EnterpriseVO> joinList() {
        return List.of();
    }

    @Override
    public boolean checkExistByName(String name, Long id) {
        return false;
    }

    @Override
    public boolean checkExistByUid(String uid) {
        return false;
    }

    @Override
    public Enterprise getEnterpriseById(Long id) {
        return this.getById(id);
    }

    @Override
    public Enterprise getEnterpriseByUid(String uid) {
        return this.baseMapper.selectOne(Wrappers.<Enterprise>lambdaQuery()
                .eq(Enterprise::getUid,uid));
    }

    @Override
    public String getUidByEnterpriseId(Long enterpriseId) {
        return getEnterpriseById(enterpriseId).getUid();
    }

    @Override
    public int updateExpireTime(Enterprise enterprise) {
        return this.baseMapper.update(Wrappers.<Enterprise>lambdaUpdate()
                .set(Enterprise::getExpireTime,enterprise.getExpireTime())
                .eq(Enterprise::getId, enterprise.getId()));
    }

    @Override
    public boolean save(Enterprise enterprise) {
        return super.save(enterprise);
    }

    @Override
    public boolean updateById(Enterprise enterprise) {
        return super.updateById(enterprise);
    }

    @Override
    public Enterprise getById(Long id) {
        return super.getById(id);
    }
}
