package com.ad.menghanyao.ad.service;

import com.ad.menghanyao.ad.dto.ListDTO;
import com.ad.menghanyao.ad.dto.ResultDTO;
import com.ad.menghanyao.ad.dto.ShopDTO;
import com.ad.menghanyao.ad.enumeration.NoticeEnum;
import com.ad.menghanyao.ad.enumeration.ResultEnum;
import com.ad.menghanyao.ad.enumeration.ShopEnum;
import com.ad.menghanyao.ad.enumeration.UserEnum;
import com.ad.menghanyao.ad.mapper.ShopMapper;
import com.ad.menghanyao.ad.model.Shop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ShopService {

    @Autowired
    private UserService userService;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private PageService pageService;

    @Autowired
    private TimeService timeService;

    public void addShop(Shop shop) {
        System.out.println("Service：调用了addShop接口，需要插入一条商店信息");
        shop.setShopStatus(ShopEnum.SHOP_STATUS_NORMAL.getCode());
        System.out.println("shop = " + shop);
        shop.setIssueCount(0L);
        shop.setClickCount(0L);
        shop.setLikeCount(0L);
        shop.setDislikeCount(0L);
        shop.setGmtCreate(System.currentTimeMillis());
        shop.setGmtModified(shop.getGmtCreate());
        shopMapper.addShop(shop);
    }

    public ListDTO<Shop> shopList(ListDTO<Shop> requestListDTO) {
        System.out.println("Service：调用了shopList接口，需要返回商店列表");

        Integer offset = pageService.getOffset(requestListDTO.getCurrent(), requestListDTO.getSize());
        Integer limit =  pageService.getSize(requestListDTO.getSize());

        Integer userType = userService.getUserType(requestListDTO.getUserId());

        Long total = serviceGetShopCount(requestListDTO.getUserId(), userType);
        List<Shop> shopList = serviceGetShopList(requestListDTO.getUserId(), userType, offset, limit);

        ListDTO<Shop> shopListDTO = new ListDTO<Shop>();
        shopListDTO.setCurrent(requestListDTO.getCurrent());
        shopListDTO.setSize(limit);
        shopListDTO.setTotal(total);
        shopListDTO.setData(shopList);
        return shopListDTO;
    }

    private List<Shop> serviceGetShopList(Long userId, Integer userType, Integer offset, Integer limit) {
        autoChangeShopStatus();
        switch (userType) {
            case 0:
            case 3:
                return shopMapper.shopListAll(offset, limit);
            case 2:
                return shopMapper.shopListByUserId(userId, offset, limit);
            case 1:
            default:
                break;
        }
        return null;
    }

    private Long serviceGetShopCount(Long userId, Integer userType) {
        switch (userType) {
            case 0:
            case 3:
                return shopMapper.shopCountAll();
            case 2:
                return shopMapper.shopCountByUserId(userId);
            case 1:
            default:
                break;
        }
        return null;
    }

    public Long shopCountToday() {
        System.out.println("Service：shopCountToday，需要返回今日商店总数");
        Long todayZeroTimestamp = timeService.getTodayTimestamp();
        Long shopCountToday = shopMapper.shopCountToday(todayZeroTimestamp);
        return shopCountToday;
    }

    public ResultDTO shopSaveMoney(ShopDTO shopDTO) {
        System.out.println("Service：shopSaveMoney，需要存钱");
        Long previousMoney = shopMapper.getShopCash(shopDTO.getShopId());
        Long newMoney = previousMoney + shopDTO.getShopCash();
        try {
            shopMapper.setShopCash(shopDTO.getShopId(), newMoney, System.currentTimeMillis());
            return new ResultDTO(ResultEnum.MONEY_SAVED.getCode(), ResultEnum.MONEY_SAVED.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(ResultEnum.MONEY_SAVE_FAILED.getCode(), ResultEnum.MONEY_SAVE_FAILED.getMessage());
        }

    }

    public ResultDTO forbidShop(Long noticeId, Long target, Long operator) {
        System.out.println("Service：forbidShop，需要forbidShop 3 days");
        if (userService.haveAuthority(operator, UserEnum.USER_TYPE_ADMIN.getCode())) {
            try {
                System.out.println("noticeId = " + noticeId + ", target = " + target + ", operator = " + operator);
                shopMapper.forbidShop(target, ShopEnum.SHOP_STATUS_FORBID.getCode(), System.currentTimeMillis());
                ResultDTO resultDTO = noticeService.updateNoticeStatus(noticeId, NoticeEnum.NOTICE_STATUS_FORBID_SHOP_3_DAYS.getCode());
                if (resultDTO.getCode().equals(ResultEnum.NOTICE_UPDATED.getCode()))
                    return new ResultDTO(ResultEnum.SHOP_FORBIDDEN.getCode(), ResultEnum.SHOP_FORBIDDEN.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                return new ResultDTO(ResultEnum.SHOP_FORBID_FAILED.getCode(), ResultEnum.SHOP_FORBID_FAILED.getMessage());
            }
        }
        return new ResultDTO(ResultEnum.NO_AUTHORITY.getCode(), ResultEnum.NO_AUTHORITY.getMessage());
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void autoChangeShopStatus(){
        List<Shop> shopList = shopMapper.shopListForbidden();
        for (Shop shop:shopList) {
            if (shop.getGmtModified() + (ShopEnum.FORBID_DAYS.getCode() * 24 * 60 * 60)
                    < timeService.getTodayTimestamp()) {
                System.out.println("执行了一次商店状态恢复操作，注意影响。");
                shopMapper.forbidOrRelieveShop(shop.getShopId(), ShopEnum.SHOP_STATUS_NORMAL.getCode() , System.currentTimeMillis());
            }
        }
    }
}
