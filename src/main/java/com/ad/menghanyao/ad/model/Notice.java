package com.ad.menghanyao.ad.model;

import lombok.Data;

@Data
public class Notice {
    private Long noticeId;
    private Long shopId;
    private Long adId;
    private Long fromId;
    private Long toId;
    private String noticeReason;
    private Integer noticeStatus;
    private Integer noticeType;
    private Long gmtCreate;
    private Long gmtModified;
}
