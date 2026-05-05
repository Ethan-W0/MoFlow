package com.ran.commons.util;


import com.ran.commons.config.JwtClaimsFilter;
import com.ran.commons.constant.ResponseEnum;
import com.ran.commons.entity.UserInfo;
import com.ran.commons.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 *  提取当前登录用户的信息
 */
public final class RequestContextUtil {

    private RequestContextUtil() {}

    public static String getUID() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            throw new BusinessException(ResponseEnum.UNAUTHORIZED);
        }
        String uid = (String) request.getAttribute(JwtClaimsFilter.USER_ID_ATTRIBUTE);
        if (StringUtils.isBlank(uid)) {
            throw new BusinessException(ResponseEnum.UNAUTHORIZED);
        }
        return uid;
    }

    public static UserInfo getUserInfo() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            throw new BusinessException(ResponseEnum.UNAUTHORIZED);
        }
        Object userInfoObj = request.getAttribute(JwtClaimsFilter.USER_INFO_ATTRIBUTE);
        if (userInfoObj instanceof UserInfo userInfo) {
            return userInfo;
        } else {
            throw new BusinessException(ResponseEnum.UNAUTHORIZED);
        }
    }

    public static HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
}