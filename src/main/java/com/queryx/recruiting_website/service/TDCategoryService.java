package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TDCategory;
import com.queryx.recruiting_website.domain.dto.CategoryDto;
import com.queryx.recruiting_website.domain.vo.CategoryVO;


public interface TDCategoryService extends IService<TDCategory> {


    IPage<CategoryVO> selectCategoryList(Integer page, Integer size, String categoryName, String status);

    Object updateCategory(CategoryDto categoryDto);

    Object updateCategoryStatus(Integer status, Long categoryId);

    Object addCategory(String categoryName);

    Object delCategory(Long categoryId);
}

