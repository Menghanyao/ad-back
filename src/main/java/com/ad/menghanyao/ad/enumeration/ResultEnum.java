package com.ad.menghanyao.ad.enumeration;

public enum ResultEnum {
    MONEY_SAVED(1,"已成功存入"),
    MONEY_SAVE_FAILED(0,"存入失败"),
    ROOT_ADDED(111,"已添加root用户"),
    ROOT_ALREADY_EXIST(112,"已有root用户"),
    ROOT_ADD_FAILED(110,"root添加失败"),
    USER_ADDED(11,"已添加用户"),
    USER_ADD_FAILED(10,"用户添加失败"),
    ADMIN_ADDED(13,"已通过管理员申请"),
    WAIT_ADDED(14,"已申请成为管理员"),

    SHOP_ADD_FAILED(20,"添加商店失败"),
    SHOP_ADDED(21,"已添加商店"),
    SHOP_FORBID_FAILED(22,"停封失败"),
    SHOP_FORBIDDEN(23,"停封3天"),

    AD_ADD_FAILED(30,"添加广告失败"),
    AD_ADDED(31,"已添加广告"),
    AD_DELETE_FAILED(32,"广告删除失败"),
    AD_DELETED(33,"已删除广告"),

    NOTICE_ADD_FAILED(40,"添加通知失败"),
    NOTICE_ADDED(41,"已添加通知"),
    NOTICE_UPDATED(42,"已更新状态"),
    NOTICE_UPDATE_FAILED(43,"状态更新失败"),

    NO_AUTHORITY(50, "没有操作权限"),

    REPORT_REJECTED(61,"已驳回"),
    REPORT_REJECT_FAILED(60,"驳回失败，请重试"),

    RECORD_GOT(71,"已添加记录"),
    RECORD_GET_FAILED(70,"记录添加失败"),

    NO_LONGER_LOGIN(100,"登录已失效，请重新输入密码"),
    LOGININ_FAILED(101,"账户或密码错误"),

    AD_READ_FAILED(110,"广告读取失败")

    ;
    private Integer code;
    private String message;

    ResultEnum(Integer code, String message) {
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
