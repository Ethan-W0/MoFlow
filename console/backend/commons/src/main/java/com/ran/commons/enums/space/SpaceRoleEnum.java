package com.ran.commons.enums.space;

public enum SpaceRoleEnum {

    OWNER(1, "Owner"), ADMIN(2, "Admin"), MEMBER(3, "Member");

    private Integer code;

    private String desc;

    SpaceRoleEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static SpaceRoleEnum getByCode(Integer code) {
        for (SpaceRoleEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
