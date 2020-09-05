package com.ad.menghanyao.ad.controller;

import com.ad.menghanyao.ad.dto.AdDTO;
import com.ad.menghanyao.ad.dto.ListDTO;
import com.ad.menghanyao.ad.dto.ResultDTO;
import com.ad.menghanyao.ad.enumeration.ResultEnum;
import com.ad.menghanyao.ad.mapper.AdMapper;
import com.ad.menghanyao.ad.model.Ad;
import com.ad.menghanyao.ad.model.Record;
import com.ad.menghanyao.ad.model.ReadModel;
import com.ad.menghanyao.ad.model.User;
import com.ad.menghanyao.ad.service.ReadService;
import com.ad.menghanyao.ad.service.RecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@ResponseBody
@CrossOrigin
public class ReadController {

    @Autowired
    private RecordService recordService;

    @Autowired
    private AdMapper adMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ReadService readService;

    @RequestMapping(value = "/readList", method = RequestMethod.POST)
    public Object  readList(@RequestBody ListDTO<User> user) {
        System.out.println("调用了readList，需要返回一堆广告");
        Long userId = user.getUserId();
        Integer offset = user.getCurrent();
        try {
            List<Ad> adList = adMapper.adListAll(offset, 1);
            return adList;
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(ResultEnum.AD_READ_FAILED.getCode(), ResultEnum.AD_READ_FAILED.getMessage());
        }
    }

    @RequestMapping(value = "/addRecord", method = RequestMethod.POST)
    public Object addRecord(@RequestBody Record record) {
        System.out.println("调用了addRecord，需要添加阅读记录");
        ResultDTO resultDTO = recordService.addRecord(record);
        return resultDTO;
    }

    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public Object read(@RequestBody ListDTO<User> user) {
        System.out.println("Controller：read，需要返回1条广告");
        Long userId = user.getUserId();

        ReadModel redisModel = new ReadModel();
        AdDTO adDTO = adMapper.getAdByAdId(357L);
        Ad ad = new Ad();
        BeanUtils.copyProperties(adDTO, ad);
//        redisModel.setNumber(1);
//        redisModel.setAd(ad);
        readService.strategy(userId);
//        redisTemplate.opsForValue().set(1,redisModel);
//        System.out.println("ad = " + redisTemplate.opsForValue().get(1));
        return null;
    }
}
