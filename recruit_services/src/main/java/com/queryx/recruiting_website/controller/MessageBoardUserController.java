package com.queryx.recruiting_website.controller;

import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.domain.dto.SendMessageDto;
import com.queryx.recruiting_website.domain.vo.LastMessageVO;
import com.queryx.recruiting_website.domain.vo.MessageDataVO;
import com.queryx.recruiting_website.service.MessageBoardService;
import com.queryx.recruiting_website.utils.CommonResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Tag(name = "用户端留言板", description = "用户端留言板接口")
@RequestMapping("/message")
public class MessageBoardUserController {


    @Resource
    private MessageBoardService messageBoardService;


    @GetMapping("info")
    @Operation(summary = "获取留言板数据", parameters = {
            @Parameter(name = "companyId", description = "公司id", required = true, in = ParameterIn.QUERY)
    }, responses = {
            @ApiResponse(responseCode = "200", description = "成功获取留言板数据", content = @Content(schema = @Schema(implementation = CommonResp.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "482", description = "缺少参数", content = @Content(schema = @Schema(implementation = CommonResp.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "512", description = "系统错误", content = @Content(schema = @Schema(implementation = CommonResp.class), mediaType = "application/json"))
    })
    public CommonResp<List<MessageDataVO>> getMessageData(@RequestParam("page") Integer page,@RequestParam("size") Integer size, @RequestParam("id") Long companyId) {
        if (companyId == null) {
            return CommonResp.fail(AppHttpCodeEnum.MISSING_PARAMETERS, null);
        }
        return CommonResp.success(messageBoardService.queryMessageData(page, size, companyId));
    }


    @GetMapping("list")
    @Operation(summary = "获取留言板列表及该用户的最后一条留言", responses = {
            @ApiResponse(responseCode = "200", description = "成功获取留言板数据", content = @Content(schema = @Schema(implementation = CommonResp.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "512", description = "系统错误", content = @Content(schema = @Schema(implementation = CommonResp.class), mediaType = "application/json"))
    })
    public CommonResp<List<LastMessageVO>> lastMessage() {
        return CommonResp.success(messageBoardService.queryMessageListAndLastMessage());
    }


    @PostMapping("send")
    @Operation(summary = "发送消息", parameters = {
            @Parameter(name = "sendMessageDto", description = "发送消息DTO", required = true, in = ParameterIn.QUERY)
    }, responses = {
            @ApiResponse(responseCode = "200", description = "成功获取留言板数据", content = @Content(schema = @Schema(implementation = CommonResp.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "512", description = "系统错误", content = @Content(schema = @Schema(implementation = CommonResp.class), mediaType = "application/json"))
    })
    public CommonResp<Boolean> sendMessage(@RequestBody SendMessageDto sendMessageDto) {
        return CommonResp.success(messageBoardService.saveMessage(sendMessageDto.getUserId(), sendMessageDto.getContent()));
    }
}
