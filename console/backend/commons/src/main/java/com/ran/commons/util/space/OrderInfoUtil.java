package com.ran.commons.util.space;

import com.ran.commons.enums.space.EnterpriseServiceTypeEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

public class OrderInfoUtil {

    public static boolean existValidEnterpriseOrder(String uid) {
        return true;
    }

    public static EnterpriseResult getEnterpriseResult(String uid) {
        return new EnterpriseResult(EnterpriseServiceTypeEnum.ENTERPRISE, LocalDateTime.now().plusDays(365));
    }

    public static boolean existValidProOrder(String uid) {
        return true;
    }

    @Data
    @Builder
    public static class EnterpriseResult {

        private EnterpriseServiceTypeEnum serviceType;

        private LocalDateTime endTime;
    }
}
