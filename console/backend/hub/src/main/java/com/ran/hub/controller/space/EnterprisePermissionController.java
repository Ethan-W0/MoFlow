package com.ran.hub.controller.space;

import com.ran.commons.service.space.EnterprisePermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/enterprise-permission")
public class EnterprisePermissionController {

    @Resource
    private EnterprisePermissionService enterprisePermissionService;

}
