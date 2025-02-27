package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.TDUser;
import com.queryx.recruiting_website.domain.vo.JobResumeVO;
import com.queryx.recruiting_website.mapper.TDJobResumeMapper;
import com.queryx.recruiting_website.mapper.TDUserMapper;
import com.queryx.recruiting_website.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import org.apache.ibatis.session.SqlSessionFactory;
import com.baomidou.mybatisplus.core.batch.MybatisBatch;
import com.queryx.recruiting_website.domain.TDJobResume;
import com.queryx.recruiting_website.service.DeliverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.queryx.recruiting_website.domain.dto.DeliverResumeDTO;

import java.util.Date;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class DeliverServiceImpl implements DeliverService {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private TDUserMapper userMapper;

    @Autowired
    private TDJobResumeMapper jobResumeMapper;

    public int insertDeliverResume(DeliverResumeDTO deliverResumeDTO) {
        TDUser user = userMapper.selectById(SecurityUtils.getLoginUser().getTdUser().getUserId());
        // 装配数据
        final TDJobResume tdJobResume = new TDJobResume();
        tdJobResume.setJobId(deliverResumeDTO.getJobId());
        tdJobResume.setResumeId(deliverResumeDTO.getResumeId());
        tdJobResume.setResumeType(deliverResumeDTO.getResumeType());
        tdJobResume.setResumeName(user.getUserName());
        tdJobResume.setResumeStatus(Common.DELIVER_RESUME_STATUS_DELIVERED);
        tdJobResume.setUserId(user.getUserId());
        tdJobResume.setDeliverDate(new Date());
        // 数据入库
        final MybatisBatch<TDJobResume> batch = new MybatisBatch<>(sqlSessionFactory, List.of(tdJobResume));
        final MybatisBatch.Method<TDJobResume> method = new MybatisBatch.Method<>(TDJobResumeMapper.class);
        int resultCount = batch.execute(method.insert()).size();
        user.setUserInterviews(user.getUserInterviews() + 1);
        userMapper.updateById(user);
        return resultCount;
    }

    @Override
    public List<JobResumeVO> queryJobResumeList(Long userId, Integer pageNum, Integer pageSize) {
//        LambdaQueryWrapper<TDJobResume> jobResumeQueryWrapper = new LambdaQueryWrapper<>();
//        jobResumeQueryWrapper.eq(TDJobResume::getUserId, userId);
//        Page<TDJobResume> tdJobResumePage = jobResumeMapper.selectPage(new Page<>(pageNum, pageSize), jobResumeQueryWrapper);


        return List.of();
    }
}
