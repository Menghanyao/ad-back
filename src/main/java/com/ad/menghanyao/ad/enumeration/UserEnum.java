package com.ad.menghanyao.ad.enumeration;

public enum UserEnum {
    USER_TYPE_ROOT(0,"root"),
    USER_TYPE_USER(1,"user"),
    USER_TYPE_ADER(2,"ader"),
    USER_TYPE_ADMIN(3,"admin"),
    USER_TYPE_WAIT(4,"wait"),

    USER_STATUS_FORBID(0,"forbid"),
    USER_STATUS_NORMAL(1,"normal"),

    ADMIN_PASSED(1,"已更改管理员权限"),
    ADMIN_PASS_FAILED(0,"更改失败"),

    ;
    private Integer code;
    private String message;

    UserEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
