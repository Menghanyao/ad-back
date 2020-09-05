package com.ad.menghanyao.ad.service;

import org.springframework.stereotype.Service;

@Service
public class PageService {

    public Integer getOffset(Integer current, Integer size) {
        return current == null ? 0 : (current-1) * size;
    }

    public Integer getSize(Integer size) {
        return size == null ? 20 : size;
    }
}
