package com.queryx.recruiting_website.service.impl;

import org.springframework.stereotype.Service;
import org.apache.ibatis.session.SqlSessionFactory;
import com.baomidou.mybatisplus.core.batch.MybatisBatch;
import com.queryx.recruiting_website.domain.TDJobResume;
import com.queryx.recruiting_website.mapper.JobResumeMapper;
import com.queryx.recruiting_website.service.DeliverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.queryx.recruiting_website.domain.dto.DeliverResumeDTO;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class DeliverServiceImpl implements DeliverService {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Override
    public int insertDeliverResume(DeliverResumeDTO deliverResumeDTO) {
        // 装配数据
        final TDJobResume tdJobResume = new TDJobResume();
        tdJobResume.setJobId(deliverResumeDTO.getJobId());
        tdJobResume.setResumeId(deliverResumeDTO.getResumeId());
        tdJobResume.setResumeType(deliverResumeDTO.getResumeType());
        tdJobResume.setResumeName(deliverResumeDTO.getResumeName());
        // 数据入库
        final MybatisBatch<TDJobResume> batch = new MybatisBatch<>(sqlSessionFactory, List.of(tdJobResume));
        final MybatisBatch.Method<TDJobResume> method = new MybatisBatch.Method<>(JobResumeMapper.class);
        return batch.execute(method.insert()).size();
    }
}
