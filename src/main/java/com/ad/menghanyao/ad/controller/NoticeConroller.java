package com.ad.menghanyao.ad.controller;

import com.ad.menghanyao.ad.dto.ListDTO;
import com.ad.menghanyao.ad.dto.NoticeDTO;
import com.ad.menghanyao.ad.dto.OperationDTO;
import com.ad.menghanyao.ad.dto.ResultDTO;
import com.ad.menghanyao.ad.enumeration.NoticeEnum;
import com.ad.menghanyao.ad.model.Notice;
import com.ad.menghanyao.ad.service.NoticeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@ResponseBody
@CrossOrigin
public class NoticeConroller {

    @Autowired
    private NoticeService noticeService;

    @RequestMapping(value = "/addNotice",method = RequestMethod.POST)
    public ResultDTO addNotice(@RequestBody NoticeDTO noticeDTO) {
        System.out.println("Controller：addNotice，需要插入一条notice信息");
        Notice notice = new Notice();
        BeanUtils.copyProperties(noticeDTO, notice);
        return noticeService.addNotice(notice);
    }

    @RequestMapping(value = "/violateList", method = RequestMethod.POST)
    public Object violateList(@RequestBody ListDTO<Notice> requestListDTO ) {
        System.out.println("Controller：violateList，需要返回violateList列表");
        System.out.println("listDTO = " + requestListDTO);
        ListDTO<Notice> responseListDTO = noticeService.violateList(requestListDTO);
        return responseListDTO;
    }

    @RequestMapping(value = "/myNoticeList", method = RequestMethod.POST)
    public Object myNoticeList(@RequestBody ListDTO<Notice> requestListDTO ) {
        System.out.println("Controller：myNoticeList，需要返回myNoticeList");
        System.out.println("listDTO = " + requestListDTO);
        ListDTO<Notice> responseListDTO = noticeService.myNoticeList(requestListDTO);
        return responseListDTO;
    }

    @RequestMapping(value = "/applyList", method = RequestMethod.POST)
    public Object applyList(@RequestBody ListDTO<Notice> requestListDTO ) {
        System.out.println("Controller：applyList，需要返回applyList列表");
        System.out.println("listDTO = " + requestListDTO);
        ListDTO<Notice> responseListDTO = noticeService.applyList(requestListDTO);
        return responseListDTO;
    }

    @RequestMapping(value = "/reject", method = RequestMethod.POST)
    public Object reject(@RequestBody OperationDTO operationDTO ) {
        System.out.println("Controller：reject，需要reject");
        System.out.println("operationDTO = " + operationDTO);
        ResultDTO resultDTO = noticeService.reject(operationDTO.getNoticeId(), operationDTO.getTarget(), operationDTO.getOperator());
        return resultDTO;
    }
}
