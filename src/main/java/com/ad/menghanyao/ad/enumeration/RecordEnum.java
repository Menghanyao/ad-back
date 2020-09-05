package com.ad.menghanyao.ad.enumeration;

public enum RecordEnum {
    REPORT(0,"report"),
    PASS(1,"pass"),
    FINISH(2,"finish"),
    LIKE(3,"like"),
    SAVE(4,"save"),
    ;
    private Integer code;
    private String message;

    RecordEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
