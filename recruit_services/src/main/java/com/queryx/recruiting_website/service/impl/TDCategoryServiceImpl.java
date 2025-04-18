package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.domain.TDCategory;
import com.queryx.recruiting_website.domain.dto.CategoryDto;
import com.queryx.recruiting_website.domain.vo.CategoryVO;
import com.queryx.recruiting_website.exception.SystemException;
import com.queryx.recruiting_website.mapper.TDCategoryMapper;
import com.queryx.recruiting_website.service.TDCategoryService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import java.util.stream.Collectors;


@Service("tDCategoryService")
public class TDCategoryServiceImpl extends ServiceImpl<TDCategoryMapper, TDCategory> implements TDCategoryService {

    @Resource
    private TDCategoryMapper tDCategoryMapper;

    @Override
    public IPage<CategoryVO> selectCategoryList(Integer page, Integer size, String categoryName, String status) {
        LambdaUpdateWrapper<TDCategory> tdCategoryLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        tdCategoryLambdaUpdateWrapper.like(StringUtils.hasText(categoryName), TDCategory::getCategoryName, categoryName)
                .eq(StringUtils.hasText(status), TDCategory::getCategoryStatus, status);
        Page<TDCategory> tdCategoryPage = tDCategoryMapper.selectPage(new Page<>(page, size), tdCategoryLambdaUpdateWrapper);
        IPage<CategoryVO> categoryPageVOPage = new Page<>(tdCategoryPage.getCurrent(), tdCategoryPage.getSize(), tdCategoryPage.getTotal());

        return categoryPageVOPage.setRecords(tdCategoryPage.getRecords().stream().map(tdCategory -> {
            CategoryVO categoryVOList = new CategoryVO();
            BeanUtils.copyProperties(tdCategory, categoryVOList);
            return categoryVOList;
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
    public Object addCategory(CategoryDto categoryDto) {
        TDCategory tdCategory = new TDCategory();
        tdCategory.setCategoryDescription(categoryDto.getCategoryDescription());
        tdCategory.setCategoryStatus(categoryDto.getCategoryStatus());
        tdCategory.setCategoryName(categoryDto.getCategoryName());
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

