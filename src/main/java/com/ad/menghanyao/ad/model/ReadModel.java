package com.ad.menghanyao.ad.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReadModel implements Serializable {
    private String source;
    private Object ad;
    private Long adId;
}
