package com.ad.menghanyao.ad.dto;

import lombok.Data;

@Data
public class ShopDTO {
    private Long shopId;
    private Long userId;
    private String shopName;
    private Integer shopType;
    private String shopDescription;
    private String shopCity;
    private String shopAddress;
    private Integer shopStatus;
    private Long shopPhone;
    private Integer shopArrange;
    private Long shopCash;
    private Long issueCount;
    private Long clickCount;
    private Long likeCount;
    private Long dislikeCount;
    private Long gmtCreate;
    private Long gmtModified;
}
