package com.queryx.recruiting_website.controller;

import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.domain.dto.DeliverResumeDTO;
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

    /**
     * 简历投递
     *
     * @param deliverResumeDTO 简历投递DTO
     * @return 成功或失败的响应
     */
    @PostMapping("/resume")
    public CommonResp<Integer> deliverResume(@RequestBody DeliverResumeDTO deliverResumeDTO) {
        try {
            final int count = deliverService.insertDeliverResume(deliverResumeDTO);
            log.info("简历投递成功");
            return CommonResp.success(count);
        } catch (Exception e) {
            log.error("简历投递失败，{}", e.getMessage());
            return CommonResp.fail(AppHttpCodeEnum.DELIVER_RESUME_FAIL, null);
        }
    }
}
