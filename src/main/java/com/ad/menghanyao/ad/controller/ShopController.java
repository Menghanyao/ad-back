package com.ad.menghanyao.ad.controller;

import com.ad.menghanyao.ad.dto.ListDTO;
import com.ad.menghanyao.ad.dto.OperationDTO;
import com.ad.menghanyao.ad.dto.ResultDTO;
import com.ad.menghanyao.ad.dto.ShopDTO;
import com.ad.menghanyao.ad.enumeration.ResultEnum;
import com.ad.menghanyao.ad.mapper.ShopMapper;
import com.ad.menghanyao.ad.model.Shop;
import com.ad.menghanyao.ad.service.ShopService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@ResponseBody
@CrossOrigin
public class ShopController {

    @Autowired
    private ShopService shopService;

    @Autowired
    private ShopMapper shopMapper;

    @RequestMapping(value = "/addShop", method = RequestMethod.POST)
    public Object addShop(@RequestBody ShopDTO shopDTO ) {
        System.out.println("Controller：addShop，需要插入一条商店信息");
        Shop shop = new Shop();
        BeanUtils.copyProperties(shopDTO, shop);
        shopService.addShop(shop);
        ResultDTO resultDTO = new ResultDTO(ResultEnum.SHOP_ADDED.getCode(), ResultEnum.SHOP_ADDED.getMessage());
        return resultDTO;
    }

    @RequestMapping(value = "/shopList", method = RequestMethod.POST)
    public Object userList(@RequestBody ListDTO<Shop> requestListDTO ) {
        System.out.println("Controller：userList，需要返回商店列表");
        System.out.println("listDTO = " + requestListDTO);
        ListDTO<Shop> responseListDTO = shopService.shopList(requestListDTO);
        return responseListDTO;
    }

    @GetMapping("/shopCount")
    public Long shopCount(){
        System.out.println("Controller：shopCount，需要返回商店总数");
        Long shopCount = shopMapper.shopCountAll();
        System.out.println(shopCount);
        return shopCount;
    }

    @GetMapping("/shopCountToday")
    public Long shopCountToday(){
        System.out.println("Controller：shopCountToday，需要返回今日商店总数");
        Long shopCountToday = shopService.shopCountToday();
        System.out.println(shopCountToday);
        return shopCountToday;
    }

    @RequestMapping(value = "/shopSaveMoney", method = RequestMethod.POST)
    public Object shopSaveMoney(@RequestBody ShopDTO shopDTO ) {
        System.out.println("Controller：shopSaveMoney，需要存钱");
        System.out.println("listDTO = " + shopDTO);
        ResultDTO resultDTO = shopService.shopSaveMoney(shopDTO);
        return resultDTO;
    }

    @RequestMapping(value = "/forbidShop", method = RequestMethod.POST)
    public Object forbidShop(@RequestBody OperationDTO operationDTO ) {
        System.out.println("Controller：forbidShop，需要封停店铺");
        System.out.println("operationDTO = " + operationDTO);
        ResultDTO resultDTO = shopService.forbidShop(operationDTO.getNoticeId(), operationDTO.getTarget(), operationDTO.getOperator());
        return resultDTO;
    }
}
