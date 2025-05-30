package com.queryx.recruiting_website.controller;

import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.domain.dto.AdminLoginDto;
import com.queryx.recruiting_website.domain.dto.LoginDTO;
import com.queryx.recruiting_website.domain.dto.RegisterDTO;
import com.queryx.recruiting_website.domain.vo.UserLoginVO;
import com.queryx.recruiting_website.service.TDAdminService;
import com.queryx.recruiting_website.service.TDUserService;
import com.queryx.recruiting_website.service.TPMenuService;
import com.queryx.recruiting_website.service.UserService;
import com.queryx.recruiting_website.utils.CommonResp;
import com.queryx.recruiting_website.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/user")
@Tag(name = "用户模块", description = "用户相关接口")
public class UserController {

    @Autowired
    private UserService userService;
    @Resource
    private TDAdminService tdAdminService;
    @Resource
    private TPMenuService menuService;

    /**
     * 用户注册
     *
     * @param registerDTO 用户注册信息
     * @return 注册结果
     */
    @Operation(summary = "用户注册", parameters = {
            @Parameter(name = "registerDTO", description = "用户注册信息", schema = @Schema(implementation = RegisterDTO.class), required = true)
    }, responses = {
            @ApiResponse(responseCode = "200", description = "注册成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "451", description = "用户已存在", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
//            @ApiResponse(responseCode = "453", description = "邮箱已存在", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
//            @ApiResponse(responseCode = "452", description = "手机号已存在", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "476", description = "手机号或邮箱输入不合法", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "512", description = "系统错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class)))

    })
    @PostMapping("/register")
    public CommonResp<UserLoginVO> register(@RequestBody RegisterDTO registerDTO) {
        if (registerDTO == null) {
            return CommonResp.fail(AppHttpCodeEnum.MISSING_PARAMETERS, null);
        }
        return userService.insertUser(registerDTO);
    }


    @Resource
    private TDUserService tdUserService;

    private static final String PHONE = "(^1[3-9]\\d{9}$)";
    private static final String EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    @PostMapping("/login")
    @Operation(summary = "登录功能", description = "公司用户以及学生用户和管理员共同接口")
    public CommonResp login(@RequestBody LoginDTO loginDTO) {
        if (loginDTO.getUsername().matches(PHONE) || loginDTO.getUsername().matches(EMAIL)) {
            return CommonResp.success(tdUserService.login(loginDTO));
        }
        AdminLoginDto adminLoginDto = new AdminLoginDto();
        BeanUtils.copyProperties(loginDTO, adminLoginDto);
        return CommonResp.success(tdAdminService.login(adminLoginDto));
    }

    @GetMapping("/getRouters")
    @Operation(summary = "拿到菜单数据")
    public CommonResp getRouters() {
        return CommonResp.success(menuService.getRouter());
    }

    @GetMapping("/menuList")
    @Operation(summary = "菜单列表")
    public CommonResp menuList() {
        return CommonResp.success(menuService.menuList());
    }

    /**
     * 用户头像上传
     *
     * @param image 头像图片
     * @return 上传结果
     */
    @Operation(summary = "用户头像上传", parameters = {
            @Parameter(name = "image", description = "头像图片", required = true)
    }, responses = {
            @ApiResponse(responseCode = "200", description = "上传成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "482", description = "缺失参数", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "484", description = "头像上传失败", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "512", description = "系统错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class)))
    })
    @PostMapping("/uploadAvatar")
    public CommonResp<String> uploadAvatar(@RequestParam("image") MultipartFile image) {
        final Long userId = SecurityUtils.getLoginUser().getTdUser().getUserId();
        // 校验参数
        if (userId == null && image == null) {
            return CommonResp.fail(AppHttpCodeEnum.MISSING_PARAMETERS, null);
        }
        // 上传头像
        return userService.uploadAvatar(userId, image);
    }

}

