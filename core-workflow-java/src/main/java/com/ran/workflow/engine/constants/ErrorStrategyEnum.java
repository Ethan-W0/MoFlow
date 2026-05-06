package com.ran.workflow.engine.constants;

import lombok.Getter;

/**
 * 出错时的三种策略：中断 / 自定义返回 / 走失败分支
 */
@Getter
public enum ErrorStrategyEnum {
    INTERUPT(0, "中断"),
    ERR_CODE(1, "错误码"),
    ERR_CONDITION(2, "错误条件"),
    ;

    private int code;
    private String msg;

    ErrorStrategyEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static ErrorStrategyEnum fromCode(int code) {
        for (ErrorStrategyEnum value : ErrorStrategyEnum.values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }

}
