package com.ad.menghanyao.ad.enumeration;

public enum NoticeEnum {
    NOTICE_STATUS_WAIT_HANDLE(0, "等待处理您的举报"),
    NOTICE_STATUS_REJECT_REPORT(1, "驳回您的举报"),
    NOTICE_STATUS_DELETE_AD(2, "删除违规广告"),
    NOTICE_STATUS_FORBID_SHOP_3_DAYS(3, "店铺封停3天"),

    NOTICE_STATUS_AUTHORIZE_WAIT(10, "等待通过管理员资格审核"),
    NOTICE_STATUS_AUTHORIZED(11, "已通过管理员资格审核"),
    NOTICE_STATUS_AUTHORIZE_REJECTED(12, "未通过管理员资格审核"),

    NOTICE_TYPE_SYSTEM(0, "系统消息"),
    NOTICE_TYPE_REPORT(1,"用户举报"),

    NNOTICE_REASON_DEFAULT(0,"广告违规"),
    ;
    private Integer code;
    private String message;

    NoticeEnum(Integer code, String message) {
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
