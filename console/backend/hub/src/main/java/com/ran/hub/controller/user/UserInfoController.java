package com.ran.hub.controller.user;

import com.ran.commons.data.UserInfoDataService;
import com.ran.commons.entity.UserInfo;
import com.ran.commons.response.ApiResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user-info")
@Slf4j
@RequiredArgsConstructor
public class UserInfoController {
    private final UserInfoDataService userInfoDataService;
    @GetMapping("/me")
    public ApiResult<UserInfo> getCurrentUserInfo(){
        UserInfo userInfo = userInfoDataService.getCurrentUserInfo();
        log.debug("Successfully retrieved current user information: uid={}", userInfo.getUid());
        return ApiResult.success(userInfo);
    }
}
