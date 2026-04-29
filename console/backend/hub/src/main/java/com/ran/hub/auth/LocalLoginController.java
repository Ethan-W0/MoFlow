package com.ran.hub.auth;

import com.ran.commons.response.ApiResult;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LocalLoginController {
    @PostMapping("/login")
    public ApiResult<Map<String,String>> login(@RequestBody LoginRequest loginRequest){
        log.info("login user is : {}",loginRequest.getUsername());

        if (loginRequest.getUsername().equals("admin")&& loginRequest.getPassword().equals("123")){
            Map<String , String> result = new HashMap<>();
            result.put("accessToken", "mock-local-dev-token");
            result.put("refreshToken", "mock-local-dev-refresh-token");
            log.info("Local login successful for user: {}", loginRequest.getUsername());
            return ApiResult.success(result);
        }
        log.warn("Local login failed for user: {}", loginRequest.getUsername());
        return ApiResult.error("用户名和密码错误");
    }


    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }
}
