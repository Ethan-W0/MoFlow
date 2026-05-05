package com.ran.commons.service.space;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ran.commons.dto.space.EnterpriseUserParam;
import com.ran.commons.dto.space.EnterpriseUserVO;
import com.ran.commons.entity.space.EnterpriseUser;
import com.ran.commons.enums.space.EnterpriseRoleEnum;

import java.util.List;

public interface EnterpriseUserService {


    EnterpriseUser getEnterpriseUserByUid(Long enterpriseId, String uid);

    Long countByEnterpriseIdAndUids(Long enterpriseId, List<String> uids);

    List<EnterpriseUser> listByEnterpriseId(Long enterpriseId);

    boolean addEnterpriseUser(Long enterpriseId, String uid, EnterpriseRoleEnum roleEnum);

    List<EnterpriseUser> listByRole(Long enterpriseId, EnterpriseRoleEnum roleEnum);

    Long countByEnterpriseId(Long enterpriseId);

    Page<EnterpriseUserVO> page(EnterpriseUserParam param);

    boolean removeById(EnterpriseUser enterpriseUser);

    boolean updateById(EnterpriseUser enterpriseUser);

}