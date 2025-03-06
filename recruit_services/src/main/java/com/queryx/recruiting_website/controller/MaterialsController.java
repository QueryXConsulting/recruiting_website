package com.queryx.recruiting_website.controller;

import com.queryx.recruiting_website.service.MaterialsService;
import com.queryx.recruiting_website.utils.CommonResp;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author：fjj
 * @Date：2025/3/6 16:10
 */
@RestController
@RequestMapping("/materials")
public class MaterialsController {

    private final MaterialsService materialsService;

    // 构造方法属性注入
    public MaterialsController(MaterialsService materialsService) {
        this.materialsService = materialsService;
    }

    @PostMapping("/upload") // TODO 测试
    public CommonResp<String> upload(@RequestParam("file") MultipartFile file) {

        return materialsService.testUpload(file);
    }




}
