package com.ran.commons.data;

import com.ran.commons.entity.UserInfo;

import java.util.Optional;

public interface UserInfoDataService {
    // 创建用户
    UserInfo createOrGetUser(UserInfo userInfo);
    /** Query user by UID */
    Optional<UserInfo> findByUid(String uid);
}
