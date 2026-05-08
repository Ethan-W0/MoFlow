package com.ran.commons.service.space.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ran.commons.entity.space.SpacePermission;
import com.ran.commons.mapper.space.SpacePermissionMapper;
import com.ran.commons.service.space.SpacePermissionService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class SpacePermissionServiceImpl extends ServiceImpl<SpacePermissionMapper, SpacePermission> implements SpacePermissionService {

    @Override
    public SpacePermission getSpacePermissionByKey(String key) {
        return this.getOne(Wrappers.<SpacePermission>lambdaQuery()
                .eq(SpacePermission::getPermissionKey, key));
    }

    @Override
    public List<String> listByKeys(Collection<String> keys) {
        return this.listObjs(Wrappers.<SpacePermission>lambdaQuery()
                .select(SpacePermission::getPermissionKey)
                .in(SpacePermission::getPermissionKey, keys));
    }

    @Override
    public void insertBatch(List<SpacePermission> spacePermissions) {
        this.saveBatch(spacePermissions);
    }
}
