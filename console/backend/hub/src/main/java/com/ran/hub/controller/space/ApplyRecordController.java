package com.ran.hub.controller.space;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ran.commons.annotation.RateLimit;
import com.ran.commons.response.ApiResult;
import com.ran.commons.annotation.space.EnterprisePreAuth;
import com.ran.commons.annotation.space.SpacePreAuth;
import com.ran.commons.dto.space.ApplyRecordParam;
import com.ran.commons.dto.space.ApplyRecordVO;
import com.ran.commons.service.space.ApplyRecordService;
import com.ran.hub.service.space.ApplyRecordBizService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/apply-record")
public class ApplyRecordController {
    @Resource
    private ApplyRecordService applyRecordService;
    @Resource
    private ApplyRecordBizService applyRecordBizService;

    @PostMapping("/join-enterprise-space")
    @EnterprisePreAuth(module = "Application Management", description = "Apply to join enterprise space", key = "ApplyRecordController_joinEnterpriseSpace_POST")
    @Operation(summary = "Apply to join enterprise space")
    @RateLimit(dimension = "USER", window = 1, limit = 1)
    public ApiResult<String> joinEnterpriseSpace(@RequestParam("spaceId") Long spaceId) {
        return applyRecordBizService.joinEnterpriseSpace(spaceId);
    }

    @PostMapping("/agree-enterprise-space")
    @SpacePreAuth(module = "Application Management", description = "Approve application to join enterprise space", requireSpaceId = true, key = "ApplyRecordController_agreeEnterpriseSpace_POST")
    @Operation(summary = "Approve application to join enterprise space")
    @RateLimit(dimension = "USER", window = 1, limit = 1)
    public ApiResult<String> agreeEnterpriseSpace(@RequestParam("applyId") Long applyId) {
        return applyRecordBizService.agreeEnterpriseSpace(applyId);
    }

    @PostMapping("/refuse-enterprise-space")
    @SpacePreAuth(module = "Application Management", description = "Reject application to join enterprise space", requireSpaceId = true, key = "ApplyRecordController_refuseEnterpriseSpace_POST")
    @Operation(summary = "Reject application to join enterprise space")
    @RateLimit(dimension = "USER", window = 1, limit = 1)
    public ApiResult<String> refuseEnterpriseSpace(@RequestParam("applyId") Long applyId) {
        return applyRecordBizService.refuseEnterpriseSpace(applyId);
    }

    @PostMapping("/page")
    @SpacePreAuth(module = "Application Management", description = "Application list", requireSpaceId = true, key = "ApplyRecordController_page_POST")
    @Operation(summary = "Application list")
    public ApiResult<Page<ApplyRecordVO>> page(@RequestBody ApplyRecordParam param) {
        return ApiResult.success(applyRecordService.page(param));
    }

}
