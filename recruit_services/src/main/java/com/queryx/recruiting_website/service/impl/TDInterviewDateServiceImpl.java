package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.TDInterviewDate;
import com.queryx.recruiting_website.domain.dto.AddInterviewDto;
import com.queryx.recruiting_website.domain.vo.InterViewDateVo;
import com.queryx.recruiting_website.mapper.TDInterviewDateMapper;
import com.queryx.recruiting_website.service.TDInterviewDateService;
import com.queryx.recruiting_website.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TDInterviewDateServiceImpl extends ServiceImpl<TDInterviewDateMapper, TDInterviewDate> implements TDInterviewDateService {
    @Resource
    private TDInterviewDateMapper tdInterviewDateMapper;
    @Override
    public Object addInterviewDate(AddInterviewDto addInterviewDto) {
        TDInterviewDate tdInterviewDate = new TDInterviewDate();
        BeanUtils.copyProperties(addInterviewDto,tdInterviewDate);
        save(tdInterviewDate);
        return null;
    }

    @Override
    public Object selectInterviewDate() {
        Long companyInfoId = SecurityUtils.getLoginUser().getTdUser().getCompanyInfoId();
        return tdInterviewDateMapper.selectList(
                new LambdaQueryWrapper<TDInterviewDate>().eq(TDInterviewDate::getCompanyId, companyInfoId)
                        .eq(TDInterviewDate::getIsDeleted, Common.NOT_DELETE)).stream().map(data->{
            InterViewDateVo interViewDateVo = new InterViewDateVo();
            BeanUtils.copyProperties(data,interViewDateVo);
            return interViewDateVo;
        }).toList();
    }

    @Override
    public Object deleteInterviewDate(Long interviewDateId) {
        removeById(interviewDateId);
        return null;
    }
}
