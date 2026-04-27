package com.ran.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ran.commons.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {
}
