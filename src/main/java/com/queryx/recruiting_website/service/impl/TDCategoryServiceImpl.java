package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.domain.TDCategory;
import com.queryx.recruiting_website.domain.dto.CategoryDto;
import com.queryx.recruiting_website.domain.vo.CategoryVo;
import com.queryx.recruiting_website.exception.SystemException;
import com.queryx.recruiting_website.mapper.TDCategoryMapper;
import com.queryx.recruiting_website.service.TDCategoryService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;


import java.util.stream.Collectors;


@Service("tDCategoryService")
public class TDCategoryServiceImpl extends ServiceImpl<TDCategoryMapper, TDCategory> implements TDCategoryService {

    @Resource
    private TDCategoryMapper tDCategoryMapper;

    @Override
    public IPage<CategoryVo> selectCategoryList(Integer page, Integer size, String categoryName, String status) {
        LambdaUpdateWrapper<TDCategory> tdCategoryLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        tdCategoryLambdaUpdateWrapper.like(categoryName != null, TDCategory::getCategoryName, categoryName)
                .eq(status != null, TDCategory::getCategoryStatus, status);
        Page<TDCategory> tdCategoryPage = tDCategoryMapper.selectPage(new Page<>(page, size), tdCategoryLambdaUpdateWrapper);
        IPage<CategoryVo> categoryPageVoPage = new Page<>(tdCategoryPage.getCurrent(), tdCategoryPage.getSize(), tdCategoryPage.getTotal());

        return categoryPageVoPage.setRecords(tdCategoryPage.getRecords().stream().map(tdCategory -> {
            CategoryVo categoryVoList = new CategoryVo();
            BeanUtils.copyProperties(tdCategory, categoryVoList);
            return categoryVoList;
        }).collect(Collectors.toList()));
    }

    @Override
    public Object updateCategory(CategoryDto categoryDto) {
        TDCategory tdCategory = new TDCategory();
        BeanUtils.copyProperties(categoryDto, tdCategory);
        UpdateWrapper<TDCategory> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("category_id", categoryDto.getCategoryId());

        if (tDCategoryMapper.update(tdCategory, updateWrapper) < 1) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        return null;
    }

    @Override
    public Object updateCategoryStatus(Integer status, Long categoryId) {
        TDCategory tdCategory = new TDCategory();
        tdCategory.setCategoryStatus(String.valueOf(status));
        LambdaUpdateWrapper<TDCategory> eq = new LambdaUpdateWrapper<TDCategory>().eq(TDCategory::getCategoryId, categoryId);
        if (!update(tdCategory, eq)) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        return null;
    }

    @Override
    public Object addCategory(String categoryName) {
        TDCategory tdCategory = new TDCategory();
        tdCategory.setCategoryName(categoryName);
        if (!save(tdCategory)) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        return null;
    }

    @Override
    public Object delCategory(Long categoryId) {
        if (tDCategoryMapper.deleteById(categoryId) < 1) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        return null;
    }
}

