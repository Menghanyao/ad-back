package com.ad.menghanyao.ad.dto;

import lombok.Data;

import java.util.List;

@Data
public class ListDTO<T> {
    private List<T> data;
    private Long total;
    private Integer size;       //前端
    private Integer current;    //前端

    private Long userId;        //前端
}
