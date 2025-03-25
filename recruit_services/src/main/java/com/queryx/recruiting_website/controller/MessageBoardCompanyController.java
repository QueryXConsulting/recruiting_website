package com.queryx.recruiting_website.controller;

import com.queryx.recruiting_website.domain.dto.SendMessageDto;
import com.queryx.recruiting_website.service.MessageBoardService;
import com.queryx.recruiting_website.utils.CommonResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;


@RestController
@Tag(name = "公司端留言板功能")
@RequestMapping("/company")
public class MessageBoardCompanyController {
    @Resource
    private MessageBoardService messageBoardService;
    @GetMapping("getMessageData")
    @Operation(summary = "获取留言板数据")
    public CommonResp getMessageData(Integer page,Integer size,Long userId){
        return CommonResp.success(messageBoardService.getMessageData(page,size,userId));
    }

    @GetMapping("lastMessage")
    @Operation(summary = "获取留言板用户的最后一条数据")
    public CommonResp lastMessage(){
        return CommonResp.success(messageBoardService.lastMessage());
    }

    @PostMapping("sendMessage")
    @Operation(summary = "发送消息")
    public CommonResp sendMessage(@RequestBody SendMessageDto sendMessageDto){
        return CommonResp.success(messageBoardService.sendMessage(sendMessageDto.getUserId(),sendMessageDto.getContent()));
    }
}
