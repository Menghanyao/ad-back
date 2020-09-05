package com.ad.menghanyao.ad.service;

import com.ad.menghanyao.ad.dto.AdDTO;
import com.ad.menghanyao.ad.dto.ListDTO;
import com.ad.menghanyao.ad.dto.ResultDTO;
import com.ad.menghanyao.ad.enumeration.NoticeEnum;
import com.ad.menghanyao.ad.enumeration.RecordEnum;
import com.ad.menghanyao.ad.enumeration.ResultEnum;
import com.ad.menghanyao.ad.enumeration.UserEnum;
import com.ad.menghanyao.ad.mapper.AdMapper;
import com.ad.menghanyao.ad.model.Ad;
import com.ad.menghanyao.ad.model.Record;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdService {

    @Autowired
    private UserService userService;

    @Autowired
    private AdMapper adMapper;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private PageService pageService;

    @Autowired
    private TimeService timeService;

    public ResultDTO addAd(Ad ad) {
        System.out.println("Service：调用了addAd接口，需要插入一条广告信息");
        ad.setAdProgress(0);
        ad.setIssueCount(0L);
        ad.setClickCount(0L);
        ad.setLikeCount(0L);
        ad.setDislikeCount(0L);
        ad.setGmtCreate(System.currentTimeMillis());
        ad.setGmtModified(ad.getGmtCreate());
        try {
            adMapper.addAd(ad);
            return new ResultDTO(ResultEnum.AD_ADDED.getCode(), ResultEnum.AD_ADDED.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResultDTO(ResultEnum.AD_ADD_FAILED.getCode(), ResultEnum.AD_ADD_FAILED.getMessage());
    }

    public ListDTO<Ad> adList(ListDTO<Ad> requestListDTO) {
        System.out.println("Service：调用了adList接口，需要返回广告列表");

        Integer offset = pageService.getOffset(requestListDTO.getCurrent(), requestListDTO.getSize());
        Integer limit =  pageService.getSize(requestListDTO.getSize());

        Integer userType = userService.getUserType(requestListDTO.getUserId());

        Long total = serviceGetAdCount(requestListDTO.getUserId(), userType);
        List<Ad> AdList = serviceGetAdList(requestListDTO.getUserId(), userType, offset, limit);

        ListDTO<Ad> adListDTO = new ListDTO<Ad>();
        adListDTO.setCurrent(requestListDTO.getCurrent());
        adListDTO.setSize(limit);
        adListDTO.setTotal(total);
        adListDTO.setData(AdList);
        return adListDTO;
    }

    private List<Ad> serviceGetAdList(Long userId, Integer userType, Integer offset, Integer limit) {
        switch (userType) {
            case 0:
            case 3:
                return adMapper.adListAll(offset, limit);
            case 2:
                return adMapper.adListByUserId(userId, offset, limit);
            case 1:
            default:
                break;
        }
        return null;
    }

    private Long serviceGetAdCount(Long userId, Integer userType) {
        switch (userType) {
            case 0:
            case 3:
                return adMapper.adCountAll();
            case 2:
                return adMapper.adCountByUserId(userId);
            case 1:
            default:
                break;
        }
        return null;
    }

    public Long adCountToday() {
        System.out.println("Service：adCountToday，需要返回今日广告总数");
        Long todayZeroTimestamp = timeService.getTodayTimestamp();
        Long adCountToday = adMapper.adCountToday(todayZeroTimestamp);
        return adCountToday;
    }

    public ResultDTO deleteAd(Long noticeId, Long target, Long operator) {
        System.out.println("Service：deleteAd，需要删除这条广告");
        if (userService.haveAuthority(operator, UserEnum.USER_TYPE_ADMIN.getCode())) {
            try {
                adMapper.deleteAd(target);
                ResultDTO resultDTO = noticeService.updateNoticeStatus(noticeId, NoticeEnum.NOTICE_STATUS_DELETE_AD.getCode());
                if (resultDTO.getCode().equals(ResultEnum.NOTICE_UPDATED.getCode()))
                    return new ResultDTO(ResultEnum.AD_DELETED.getCode(), ResultEnum.AD_DELETED.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                return new ResultDTO(ResultEnum.AD_DELETE_FAILED.getCode(), ResultEnum.AD_DELETE_FAILED.getMessage());
            }
        }
        return new ResultDTO(ResultEnum.NO_AUTHORITY.getCode(), ResultEnum.NO_AUTHORITY.getMessage());
    }

    public void updateAd(Record record) {
        System.out.println("service:updateAd = 需要 更新广告数据 ");
        Long adId = record.getAdId();
        if (record.getOperation() > RecordEnum.PASS.getCode()) {
            userService.updateUserCash(record.getUserId());
        }
        AdDTO dbAd = adMapper.getAdByAdId(adId);
        Ad ad = new Ad();
        BeanUtils.copyProperties(dbAd, ad);
        switch (record.getOperation()) {
            case 4:
                ad.setDislikeCount(ad.getDislikeCount()+1);
            case 2:
                ad.setClickCount(ad.getClickCount()+1);
            case 1:
                ad.setIssueCount(ad.getIssueCount()+1);
                break;
            case 3:
                ad.setIssueCount(ad.getIssueCount()+1);
                ad.setLikeCount(ad.getLikeCount()+1);
                ad.setLikeCount(ad.getLikeCount()+1);
            default:
                break;
        }
        ad.setAdProgress(Math.toIntExact(10 * ad.getClickCount() / ad.getAdCash()));
        ad.setGmtModified(System.currentTimeMillis());
        adMapper.updateAd(ad);
    }
}
