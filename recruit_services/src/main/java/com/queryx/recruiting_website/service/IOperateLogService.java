package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.OperateLog;

public interface IOperateLogService extends IService<OperateLog> {
    Object getLog(Integer page, Integer size, String operateName, String startTime, String endTime);
}
