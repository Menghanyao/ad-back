package com.ad.menghanyao.ad.dto;

import lombok.Data;

@Data
public class NoticeDTO {
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
