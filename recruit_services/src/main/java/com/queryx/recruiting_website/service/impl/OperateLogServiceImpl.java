package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.domain.OperateLog;
import com.queryx.recruiting_website.mapper.OperateLogMapper;
import com.queryx.recruiting_website.service.IOperateLogService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class OperateLogServiceImpl extends ServiceImpl<OperateLogMapper, OperateLog> implements IOperateLogService {
    @Override
    public Object getLog(Integer page, Integer size, String operateName, String startTime, String endTime) {
        LambdaQueryWrapper<OperateLog> operateLogLambdaQueryWrapper = new LambdaQueryWrapper<>();
        operateLogLambdaQueryWrapper.like(StringUtils.hasText(operateName),OperateLog::getOperateName,operateName);
        // 日期范围查询
        if (StringUtils.hasText(startTime) && StringUtils.hasText(endTime)) {
            operateLogLambdaQueryWrapper.ge(OperateLog::getGmtCreate, startTime)
                    .le(OperateLog::getGmtCreate, endTime);
        }
        return page(new Page<>(page,size), operateLogLambdaQueryWrapper);
    }
}
