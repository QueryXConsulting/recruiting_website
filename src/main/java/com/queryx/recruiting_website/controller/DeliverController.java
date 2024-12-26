package com.queryx.recruiting_website.controller;

import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.domain.DeliverResumeDTO;
import com.queryx.recruiting_website.service.DeliverService;
import com.queryx.recruiting_website.utils.CommonResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/deliver")
public class DeliverController {

    @Autowired
    private DeliverService deliverService;

    @PostMapping("/resume")
    public CommonResp<Integer> deliverResume(@RequestBody DeliverResumeDTO deliverResumeDTO) {
        try {
            final int count = deliverService.insertDeliverResume(deliverResumeDTO);
            log.info("简历投递成功");
            return new CommonResp<>(
                    AppHttpCodeEnum.SUCCESS.getCode(),
                    AppHttpCodeEnum.SUCCESS.getMsg(),
                    count
            );
        } catch (Exception e) {
            log.error("简历投递失败，{}", e.getMessage());
            return new CommonResp<>(
                    AppHttpCodeEnum.SYSTEM_ERROR.getCode(),
                    AppHttpCodeEnum.SYSTEM_ERROR.getMsg(),
                    null
            );
        }
    }
}
