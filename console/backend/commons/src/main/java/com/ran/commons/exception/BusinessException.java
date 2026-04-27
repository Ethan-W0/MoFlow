package com.ran.commons.exception;

import com.ran.commons.constant.ResponseEnum;
import com.ran.commons.util.I18nUtil;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{
    private final int code;
    private final String messageKey;
    private final ResponseEnum responseEnum;
    private final String[] args;

    public BusinessException(ResponseEnum responseEnum) {
        super(formatMessage(responseEnum.getMessageKey()));
        this.code = responseEnum.getCode();
        this.messageKey = responseEnum.getMessageKey();
        this.responseEnum = responseEnum;
        this.args = new String[0];
    }
    public BusinessException(ResponseEnum responseEnum, String... args) {
        super(formatMessage(responseEnum.getMessageKey(), args));
        this.code = responseEnum.getCode();
        this.messageKey = responseEnum.getMessageKey();
        this.responseEnum = responseEnum;
        this.args = args != null ? args : new String[0];
    }

    public BusinessException(ResponseEnum responseEnum, Throwable cause, String... args) {
        super(formatMessage(responseEnum.getMessageKey(), args), cause);
        this.code = responseEnum.getCode();
        this.messageKey = responseEnum.getMessageKey();
        this.responseEnum = responseEnum;
        this.args = args != null ? args : new String[0];
    }


    private static String formatMessage(String template, String... args) {
        return I18nUtil.getMessage(template, args);
    }
}
