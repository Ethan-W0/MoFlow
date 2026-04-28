package com.ran.hub.controller;

import com.ran.commons.response.ApiResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {
    @GetMapping
    public ApiResult<String> health(){
        return ApiResult.success();
    }
}
