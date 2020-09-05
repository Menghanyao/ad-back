package com.ad.menghanyao.ad.service;

import com.ad.menghanyao.ad.dto.ListDTO;
import com.ad.menghanyao.ad.dto.ResultDTO;
import com.ad.menghanyao.ad.enumeration.NoticeEnum;
import com.ad.menghanyao.ad.enumeration.ResultEnum;
import com.ad.menghanyao.ad.enumeration.UserEnum;
import com.ad.menghanyao.ad.mapper.NoticeMapper;
import com.ad.menghanyao.ad.model.Notice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@Service
@Transactional
@CrossOrigin
public class NoticeService {

    @Autowired
    private UserService userService;

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private PageService pageService;

    public ResultDTO addNotice(Notice notice) {
        if (notice.getNoticeType() == null) {
            notice.setNoticeType(NoticeEnum.NOTICE_TYPE_REPORT.getCode());
            notice.setNoticeStatus(NoticeEnum.NOTICE_STATUS_WAIT_HANDLE.getCode());
            if (notice.getNoticeReason() == null)
                notice.setNoticeReason(NoticeEnum.NNOTICE_REASON_DEFAULT.getMessage());
        }
        notice.setGmtCreate(System.currentTimeMillis());
        notice.setGmtModified(notice.getGmtCreate());
        System.out.println("notice = " + notice);
        try {
            noticeMapper.addNotice(notice);
            return new ResultDTO(ResultEnum.NOTICE_ADDED.getCode(), ResultEnum.NOTICE_ADDED.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(ResultEnum.NOTICE_ADD_FAILED.getCode(), ResultEnum.NOTICE_ADD_FAILED.getMessage());
        }
    }

    public ListDTO<Notice> violateList(ListDTO<Notice> requestListDTO) {
        System.out.println("Service：调用了violateList接口，需要返回violateList");

        Integer offset = pageService.getOffset(requestListDTO.getCurrent(), requestListDTO.getSize());
        Integer limit =  pageService.getSize(requestListDTO.getSize());

        Integer userType = userService.getUserType(requestListDTO.getUserId());

        Long total = serviceGetViolateCount(requestListDTO.getUserId(), userType);
        List<Notice> AdList = serviceGetViolateList(requestListDTO.getUserId(), userType, offset, limit);

        ListDTO<Notice> noticeListDTO = new ListDTO<Notice>();
        noticeListDTO.setCurrent(requestListDTO.getCurrent());
        noticeListDTO.setSize(limit);
        noticeListDTO.setTotal(total);
        noticeListDTO.setData(AdList);
        return noticeListDTO;
    }

    private List<Notice> serviceGetViolateList(Long userId, Integer userType, Integer offset, Integer limit) {
        switch (userType) {
            case 0:
            case 3:
                return noticeMapper.getAllViolateList(offset, limit);
            case 1:
            case 2:
            case 4:
                return noticeMapper.getNoticeListByToId(userId, offset, limit);
            default:
                break;
        }
        return null;
    }

    private Long serviceGetViolateCount(Long userId, Integer userType) {
        switch (userType) {
            case 0:
            case 3:
                return noticeMapper.getAllViolateCount();
            case 2:
                return noticeMapper.getNoticeCountByToId(userId);
            default:
                break;
        }
        return null;
    }

    public ListDTO<Notice> applyList(ListDTO<Notice> requestListDTO) {
        System.out.println("Service：调用了applyList接口，需要返回applyList");
        Integer offset = pageService.getOffset(requestListDTO.getCurrent(), requestListDTO.getSize());
        Integer limit =  pageService.getSize(requestListDTO.getSize());
        Integer userType = userService.getUserType(requestListDTO.getUserId());
        if (userType.equals(UserEnum.USER_TYPE_ROOT.getCode())) {
            Long total = noticeMapper.getAllApplyCount();
            List<Notice> AdList = noticeMapper.getAllApplyList(offset, limit);
            ListDTO<Notice> noticeListDTO = new ListDTO<Notice>();
            noticeListDTO.setCurrent(requestListDTO.getCurrent());
            noticeListDTO.setSize(limit);
            noticeListDTO.setTotal(total);
            noticeListDTO.setData(AdList);
            return noticeListDTO;
        }
        return null;
    }

    public ResultDTO updateNoticeStatus(Long noticeId, Integer noticeStatus) {
        try {
            noticeMapper.updateNoticeStatus(noticeId, noticeStatus, System.currentTimeMillis());
            return new ResultDTO(ResultEnum.NOTICE_UPDATED.getCode(),ResultEnum.NOTICE_UPDATED.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(ResultEnum.NOTICE_UPDATE_FAILED.getCode(),ResultEnum.NOTICE_UPDATE_FAILED.getMessage());
        }
    }

    public ListDTO<Notice> myNoticeList(ListDTO<Notice> requestListDTO) {
        Integer offset = pageService.getOffset(requestListDTO.getCurrent(), requestListDTO.getSize());
        Integer limit =  pageService.getSize(requestListDTO.getSize());

        Long total = noticeMapper.getNoticeCountByToId(requestListDTO.getUserId());
        List<Notice> AdList = noticeMapper.getNoticeListByToId(requestListDTO.getUserId(), offset, limit);

        ListDTO<Notice> noticeListDTO = new ListDTO<Notice>();
        noticeListDTO.setCurrent(requestListDTO.getCurrent());
        noticeListDTO.setSize(limit);
        noticeListDTO.setTotal(total);
        noticeListDTO.setData(AdList);
        return noticeListDTO;
    }

    public ResultDTO reject(Long noticeId, Long target, Long operator) {
        System.out.println("Service：forbidShop，需要forbidShop 3 days");
        if (userService.haveAuthority(operator, UserEnum.USER_TYPE_ADMIN.getCode())) {
            try {
                ResultDTO resultDTO = updateNoticeStatus(noticeId, NoticeEnum.NOTICE_STATUS_REJECT_REPORT.getCode());
                if (resultDTO.getCode().equals(ResultEnum.NOTICE_UPDATED.getCode()))
                    return new ResultDTO(ResultEnum.REPORT_REJECTED.getCode(), ResultEnum.REPORT_REJECTED.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                return new ResultDTO(ResultEnum.REPORT_REJECT_FAILED.getCode(), ResultEnum.REPORT_REJECT_FAILED.getMessage());
            }
        }
        return new ResultDTO(ResultEnum.NO_AUTHORITY.getCode(), ResultEnum.NO_AUTHORITY.getMessage());
    }
}
