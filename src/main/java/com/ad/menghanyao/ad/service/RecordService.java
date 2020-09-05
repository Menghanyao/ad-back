package com.ad.menghanyao.ad.service;

import com.ad.menghanyao.ad.dto.ResultDTO;
import com.ad.menghanyao.ad.enumeration.ResultEnum;
import com.ad.menghanyao.ad.mapper.RecordMapper;
import com.ad.menghanyao.ad.model.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RecordService {

    @Autowired
    private RecordMapper recordMapper;

    @Autowired
    private AdService adService;

    @Autowired
    private UserService userService;

    public ResultDTO addRecord(Record record) {
        System.out.println("service:addRecord =需要添加阅读记录 ");
        record.setGmtCreate(System.currentTimeMillis());
        try {
            recordMapper.addRecord(record);
            adService.updateAd(record);
            return new ResultDTO(ResultEnum.RECORD_GOT.getCode(), ResultEnum.RECORD_GOT.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(ResultEnum.RECORD_GET_FAILED.getCode(), ResultEnum.RECORD_GET_FAILED.getMessage());
        }
    }

}
