package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.domain.TDCategory;
import com.queryx.recruiting_website.domain.TDJobNature;
import com.queryx.recruiting_website.domain.dto.JobNatureDto;
import com.queryx.recruiting_website.domain.vo.NatureVo;
import com.queryx.recruiting_website.exception.SystemException;
import com.queryx.recruiting_website.mapper.TDJobNatureMapper;
import com.queryx.recruiting_website.service.TDJobNatureService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("tDJobNatureService")
public class TDJobNatureServiceImpl extends ServiceImpl<TDJobNatureMapper, TDJobNature> implements TDJobNatureService {

    @Resource
    private TDJobNatureMapper jobNatureMapper;

    @Override
    public List<NatureVo> selectJobNatureList() {
        List<TDJobNature> natureList = list();

        return natureList.stream().map(nature -> {
            NatureVo natureVo = new NatureVo();
            BeanUtils.copyProperties(nature, natureVo);
            return natureVo;
        }).toList();
    }

    @Override
    public NatureVo selectNatureInfo(Long jobNatureId) {
        TDJobNature byId = getById(jobNatureId);
        NatureVo natureVo = new NatureVo();
        BeanUtils.copyProperties(byId, natureVo);
        return natureVo;
    }

    @Override
    public Object updateJobNatureStatus(Integer status, Long jobNatureId) {
        TDJobNature jobNature = new TDJobNature();
        jobNature.setNatureStatus(String.valueOf(status));
        LambdaUpdateWrapper<TDJobNature> eq = new LambdaUpdateWrapper<TDJobNature>().eq(TDJobNature::getNatureId, jobNatureId);
        if (!update(jobNature, eq)) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        return null;
    }

    @Override
    public Object updateJobNature(JobNatureDto jobNatureDto) {
        TDJobNature jobNature = new TDJobNature();
        BeanUtils.copyProperties(jobNatureDto, jobNature);
        LambdaUpdateWrapper<TDJobNature> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(TDJobNature::getNatureId, jobNatureDto.getNatureId());

        if (jobNatureMapper.update(jobNature, updateWrapper) < 1) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        return null;
    }

    @Override
    public Object addJobNature(String jobNatureName) {
        TDJobNature jobNature = new TDJobNature();
        jobNature.setJobNatureName(jobNatureName);
        if (!save(jobNature)) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        return null;
    }

    @Override
    public Object delJobNature(Long jobNatureId) {
        if (jobNatureMapper.deleteById(jobNatureId) < 1) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        return null;
    }


}

