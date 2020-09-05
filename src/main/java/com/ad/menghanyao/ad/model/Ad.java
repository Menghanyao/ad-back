package com.ad.menghanyao.ad.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Ad implements Serializable {
    private Long userId;
    private Long shopId;
    private Long adId;
    private String adName;
    private String adDescription;
    private String adPicture;
    private Integer adType;
    private Integer adTime;
    private Integer adProgress;
    private Long adCash;
    private Long issueCount;
    private Long clickCount;
    private Long likeCount;
    private Long dislikeCount;
    private Long gmtCreate;
    private Long gmtModified;
}
