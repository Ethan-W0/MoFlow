package com.ran.hub.controller.space;

import com.ran.commons.dto.space.EnterpriseVO;
import com.ran.commons.response.ApiResult;
import com.ran.commons.service.space.EnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/enterprise")

public class EnterpriseController {
    @Resource
    private EnterpriseService enterpriseService;

    @Resource
    private EnterpriseBizService enterpriseBizService;

    @GetMapping("/join-list")
    @Operation(summary = "All teams")
    public ApiResult<List<EnterpriseVO>> joinList() {
        return ApiResult.success(enterpriseService.joinList());
    }

    @GetMapping("/check-need-create-team")
    @Operation(summary = "Check if team creation is needed", description = "Returns 0: No need to create team, Returns 1: Need to create team, Returns 2: Need to create enterprise team")
    public ApiResult<Integer> checkNeedCreateTeam() {
        return ApiResult.success(enterpriseService.checkNeedCreateTeam());
    }

    @GetMapping("/visit-enterprise")
    @Operation(summary = "Visit enterprise team")
    public ApiResult<Boolean> visitEnterprise(@RequestParam(value = "enterpriseId", required = false) Long enterpriseId) {
        return enterpriseBizService.visitEnterprise(enterpriseId);
    }

}
