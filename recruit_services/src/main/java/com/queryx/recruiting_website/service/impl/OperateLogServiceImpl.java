package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.domain.OperateLog;
import com.queryx.recruiting_website.mapper.OperateLogMapper;
import com.queryx.recruiting_website.service.IOperateLogService;
import org.springframework.stereotype.Service;

@Service
public class OperateLogServiceImpl extends ServiceImpl<OperateLogMapper, OperateLog> implements IOperateLogService {
}
