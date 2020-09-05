package com.ad.menghanyao.ad.controller;

import com.ad.menghanyao.ad.dto.AdDTO;
import com.ad.menghanyao.ad.dto.ListDTO;
import com.ad.menghanyao.ad.dto.OperationDTO;
import com.ad.menghanyao.ad.dto.ResultDTO;
import com.ad.menghanyao.ad.mapper.AdMapper;
import com.ad.menghanyao.ad.model.Ad;
import com.ad.menghanyao.ad.provider.UFileProvider;
import com.ad.menghanyao.ad.service.AdService;
import okhttp3.Response;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;

@Controller
@ResponseBody
@CrossOrigin
public class AdController {

    @Autowired
    private AdService adService;

    @Autowired
    private AdMapper adMapper;

    @RequestMapping(value = "/addAd",method = RequestMethod.POST)
    public Object addAd(@RequestBody AdDTO adDTO) {
        System.out.println("Controller：调用了addAd接口，需要插入一条广告信息");
        Ad ad = new Ad();
        BeanUtils.copyProperties(adDTO, ad);
        ResultDTO resultDTO = adService.addAd(ad);
        return resultDTO;
    }

    @RequestMapping(value = "/adList", method = RequestMethod.POST)
    public Object adList(@RequestBody ListDTO<Ad> requestListDTO ) {
        System.out.println("Controller：adList，需要返回广告列表");
        System.out.println("listDTO = " + requestListDTO);
        ListDTO<Ad> responseListDTO = adService.adList(requestListDTO);
        return responseListDTO;
    }

    @GetMapping("/adCount")
    public Long adCount(){
        System.out.println("Controller：adCount，需要返回广告总数");
        Long adCount = adMapper.adCountAll();
        System.out.println(adCount);
        return adCount;
    }

    @GetMapping("/adCountToday")
    public Long adCountToday(){
        System.out.println("Controller：adCountToday，需要返回今日广告总数");
        Long adCountToday = adService.adCountToday();
        System.out.println(adCountToday);
        return adCountToday;
    }

    @RequestMapping(value = "/deleteAd", method = RequestMethod.POST)
    public ResultDTO deleteAd(@RequestBody OperationDTO operationDTO) {
        System.out.println("Controller：deleteAd，需要删除这条广告");
        return adService.deleteAd(operationDTO.getNoticeId(), operationDTO.getTarget(), operationDTO.getOperator());
    }


    @RequestMapping(value = "/getAdByAdId", method = RequestMethod.POST)
    public Object getAdByAdId(@RequestBody AdDTO adDTO) {
        System.out.println("Controller：getAdByAdId，需要返回这条广告");
        AdDTO response = adMapper.getAdByAdId(adDTO.getAdId());
        return response;
    }

    @Autowired
    private UFileProvider uFileProvider;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String upload(@RequestParam("file") MultipartFile file) throws IOException {
        System.out.println("file = " + file.getOriginalFilename());
        String url = uFileProvider.upload(file.getInputStream(), file.getContentType(), file.getOriginalFilename());
        return url;
    }

}
