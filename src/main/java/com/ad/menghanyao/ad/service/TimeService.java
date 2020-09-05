package com.ad.menghanyao.ad.service;

import com.ad.menghanyao.ad.dto.SevenDayDTO;
import com.ad.menghanyao.ad.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.TimeZone;

@Service
public class TimeService {

    @Autowired
    private UserMapper userMapper;

    public Long getTodayTimestamp() {
        long current = System.currentTimeMillis();
        long daytime = current - (current + TimeZone.getDefault().getRawOffset()) % (1000*3600*24);
        System.out.println("当天凌晨(毫秒)" + daytime);
        return daytime;
    }

    public Object getSevenDayCount() {
        Long oneDay = 24 * 60 * 60 * 1000L;
        Long todayZero = getTodayTimestamp();
        Long [] userCount = new Long[7];
        Long [] shopCount = new Long[7];
        Long [] adCount = new Long[7];
        for (Integer i = 0 ; i < 7 ; i++) {
            Long start = todayZero - i * oneDay;
            Long end = start +oneDay;
            Long user ,shop, ad = null;
            user = userMapper.getUserCountBetween(start , end);
            shop = userMapper.getShopCountBetween(start , end);
            ad = userMapper.getAdCountBetween(start , end);
            userCount[i] = user;
            shopCount[i] = shop;
            adCount[i] = ad;
        }
        SevenDayDTO sevenDayDTO = new SevenDayDTO();
        sevenDayDTO.setUserCount(userCount);
        sevenDayDTO.setShopCount(shopCount);
        sevenDayDTO.setAdCount(adCount);
        System.out.println("sevenDayDTO = " + sevenDayDTO);
        return sevenDayDTO;
    }
}
