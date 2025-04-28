package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.TDCompanyInfo;
import com.queryx.recruiting_website.domain.TDJob;
import com.queryx.recruiting_website.domain.TDUser;
import com.queryx.recruiting_website.domain.vo.JobResumeVO;
import com.queryx.recruiting_website.mapper.TDCompanyInfoMapper;
import com.queryx.recruiting_website.mapper.TDJobMapper;
import com.queryx.recruiting_website.mapper.TDJobResumeMapper;
import com.queryx.recruiting_website.mapper.TDUserMapper;
import com.queryx.recruiting_website.utils.CommonResp;
import com.queryx.recruiting_website.utils.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.apache.ibatis.session.SqlSessionFactory;
import com.baomidou.mybatisplus.core.batch.MybatisBatch;
import com.queryx.recruiting_website.domain.TDJobResume;
import com.queryx.recruiting_website.service.DeliverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.queryx.recruiting_website.domain.dto.DeliverResumeDTO;

import java.util.ArrayList;
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

    @Autowired
    private TDJobMapper jobMapper;
    @Autowired
    private TDCompanyInfoMapper companyInfoMapper;

    public boolean insertDeliverResume(DeliverResumeDTO deliverResumeDTO) {
        final TDUser user = userMapper.selectById(SecurityUtils.getLoginUser().getTdUser().getUserId());
        // 装配数据
        final TDJobResume tdJobResume = new TDJobResume();
        tdJobResume.setJobResumeId(deliverResumeDTO.getJobResumeId());
        if (deliverResumeDTO.getJobResumeId() == null || !Common.DELIVER_RESUME_STATUS_REVOKE.equals(deliverResumeDTO.getResumeStatus())) {
            // 新增记录
            tdJobResume.setJobId(deliverResumeDTO.getJobId());
            tdJobResume.setResumeId(deliverResumeDTO.getResumeId());
            tdJobResume.setResumeType(deliverResumeDTO.getResumeType());
            tdJobResume.setResumeName(user.getUserName());
            tdJobResume.setUserId(user.getUserId());
            tdJobResume.setDeliverDate(new Date());
            tdJobResume.setResumeDelete("1");// 等于0表示不合适
        }
        tdJobResume.setResumeStatus(deliverResumeDTO.getResumeStatus());
        if (Common.DELIVER_RESUME_STATUS_REVOKE.equals(deliverResumeDTO.getResumeStatus())) {
            tdJobResume.setResumeDelete(Common.DELIVER_RESUME_DELETE_SQUARE_PEG);
        }
        // 数据入库
        boolean result = jobResumeMapper.insertOrUpdate(tdJobResume);
        user.setUserInterviews(user.getUserInterviews() + 1);
        userMapper.updateById(user);
        return result;
    }

    @Override
    public CommonResp<Page<JobResumeVO>> queryJobResumeList(Long userId, Integer pageNum, Integer pageSize) {
        // 构建查询投递简历记录sql语句
        LambdaQueryWrapper<TDJobResume> jobResumeQueryWrapper = new LambdaQueryWrapper<>();
        jobResumeQueryWrapper.eq(TDJobResume::getUserId, userId);
        // 执行分页查询
        Page<TDJobResume> tdJobResumePage = jobResumeMapper.selectPage(new Page<>(pageNum, pageSize), jobResumeQueryWrapper);
        if (tdJobResumePage == null) return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, null);
        Page<JobResumeVO> jobResumeVOPage = new Page<>(tdJobResumePage.getCurrent(), tdJobResumePage.getSize(), tdJobResumePage.getTotal());
        ArrayList<JobResumeVO> jobResumeVOS = new ArrayList<>();
        // 装配数据
        for (TDJobResume tdJobResume : tdJobResumePage.getRecords()) {
            JobResumeVO jobResumeVO = new JobResumeVO();
            BeanUtils.copyProperties(tdJobResume, jobResumeVO);

            // 装配job信息
            TDJob tdJob = jobMapper.selectOne(new LambdaQueryWrapper<TDJob>()
                    .select(TDJob::getJobPosition, TDJob::getCompanyId)
                    .eq(TDJob::getJobId, tdJobResume.getJobId()));
            jobResumeVO.setJobPosition(tdJob.getJobPosition());

            TDCompanyInfo companyInfo = companyInfoMapper.selectOne(new LambdaQueryWrapper<TDCompanyInfo>()
                    .select(TDCompanyInfo::getCompanyInfoName)
                    .eq(TDCompanyInfo::getCompanyInfoId, tdJob.getCompanyId()));
            jobResumeVO.setCompanyInfoName(companyInfo.getCompanyInfoName());
            // 添加记录
            jobResumeVOS.add(jobResumeVO);
        }
        jobResumeVOPage.setRecords(jobResumeVOS);
        return CommonResp.success(jobResumeVOPage);
    }
}
