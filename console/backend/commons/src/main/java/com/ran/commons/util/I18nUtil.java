package com.ran.commons.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Locale;

@Slf4j
public class I18nUtil {
    private I18nUtil(){}

    public static String getMessage(String msgKey) {
        return getMessage(msgKey, null);
    }

    public static String getMessage(String msgKey, String[] args) {
        try {
            Locale locale = getRequestLocale();
            ApplicationContext applicationContext = SpringContextHolder.getApplicationContext();
            if (applicationContext != null) {
                return applicationContext.getMessage(msgKey, args, msgKey, locale);
            }
        } catch (Exception e) {
            log.warn("Failed to get message for key: {}, falling back to key itself", msgKey, e);
        }
        return msgKey;
    }

    private static Locale getRequestLocale() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return request.getLocale();
            }
        } catch (Exception e) {
            log.debug("Failed to get locale from request context, falling back to en_US", e);
        }
        return Locale.US;
    }
    public static String getLanguage() {
        return getRequestLocale().getLanguage().toLowerCase();
    }
}
