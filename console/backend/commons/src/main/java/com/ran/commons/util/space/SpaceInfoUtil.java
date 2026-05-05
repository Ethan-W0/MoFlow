package com.ran.commons.util.space;

import com.ran.commons.service.space.EnterpriseSpaceService;
import com.ran.commons.util.RequestContextUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class SpaceInfoUtil {

    private static String spaceIdKey = "space-id";

    private static EnterpriseSpaceService enterpriseSpaceService;

    public static void init(EnterpriseSpaceService service, String key) {
        if (service == null) {
            throw new IllegalArgumentException("EnterpriseSpaceService cannot be null");
        }
        SpaceInfoUtil.enterpriseSpaceService = service;
        if (key != null && !key.trim().isEmpty()) {
            SpaceInfoUtil.spaceIdKey = key;
        } else {
            throw new IllegalArgumentException("spaceIdKey cannot be null or empty");
        }
    }

    public static String getUidByCurrentSpaceId() {
        String currentUid = RequestContextUtil.getUID();
        Long spaceId = getSpaceId();
        if (spaceId == null) {
            return currentUid;
        }
        String uid = enterpriseSpaceService.getUidByCurrentSpaceId(spaceId);
        return StringUtils.isBlank(uid) ? currentUid : uid;
    }

    public static String getUidBySpaceId(Long spaceId) {
        if (spaceId == null) {
            return null;
        }
        String uid = enterpriseSpaceService.getUidByCurrentSpaceId(spaceId);
        return StringUtils.isBlank(uid) ? null : uid;
    }

    public static Long getSpaceId() {
        HttpServletRequest request = RequestContextUtil.getCurrentRequest();
        String spaceId = request.getHeader(spaceIdKey);
        try {
            return Long.parseLong(spaceId);
        } catch (NumberFormatException e) {
            log.debug("SpaceInfoUtil.getSpaceId() failed to parse spaceId: {}, return null", spaceId);
            return null;
        }
    }

    public static boolean checkUserBelongSpace() {
        Long spaceId = getSpaceId();
        String uid = RequestContextUtil.getUID();
        if (spaceId == null) {
            return false;
        }
        return enterpriseSpaceService.checkUserBelongSpace(spaceId, uid) != null;
    }
}
