package com.ad.menghanyao.ad.model;

import lombok.Data;

@Data
public class Record {
    private Long recordId;
    private Long userId;
    private Long adId;
    private Integer adType;
    private Integer operation;
    private Long gmtCreate;

    public int getOperation(Long userId, Long adId) {
        if (userId == this.userId && adId == this.adId) {
            return this.operation;
        } else return 0;
    }
}
