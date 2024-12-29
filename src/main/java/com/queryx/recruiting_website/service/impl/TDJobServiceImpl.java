package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.TDJob;
import com.queryx.recruiting_website.exception.SystemException;
import com.queryx.recruiting_website.mapper.TDJobMapper;
import com.queryx.recruiting_website.service.TDJobService;
import com.queryx.recruiting_website.vo.JobCompanyListVo;
import com.queryx.recruiting_website.vo.JobDetailVo;
import com.queryx.recruiting_website.vo.JobInsertVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.stream.Collectors;


/**
 * (TDJob)表服务实现类
 *
 * @author makejava
 * @since 2024-12-23 13:11:00
 */
@Service("tDJobService")
public class TDJobServiceImpl extends ServiceImpl<TDJobMapper, TDJob> implements TDJobService {
    @Resource
    private TDJobMapper tdJobMapper;

    @Override
    public IPage<JobCompanyListVo> selectJobList(Integer page, Integer size, Long companyId) {
        QueryWrapper<TDJob> wrapper = new QueryWrapper<>();
        wrapper.eq("company_id", companyId)
                .select("job_id", "job_position", "job_education", "job_experience", "job_time", "job_nature", "job_review", "job_status");
        Page<TDJob> pageVo = new Page<>(page - 1, size);
        IPage<TDJob> jobPage = tdJobMapper.selectPage(pageVo, wrapper);
        if (jobPage.getRecords().isEmpty()) {
            return null;
        }

        IPage<JobCompanyListVo> jobCompanyListVoPage = new Page<>(jobPage.getCurrent(), jobPage.getSize(), jobPage.getTotal());
        jobCompanyListVoPage.setRecords(jobPage.getRecords().stream().map(tdJob -> {
            JobCompanyListVo jobCompanyListVo = new JobCompanyListVo();
            BeanUtils.copyProperties(tdJob, jobCompanyListVo);
            return jobCompanyListVo;
        }).collect(Collectors.toList()));

        return jobCompanyListVoPage;
    }

    @Override
    public JobDetailVo selectJobInfo(Long jobId) {
        LambdaQueryWrapper<TDJob> jobInfo = new LambdaQueryWrapper<TDJob>().eq(TDJob::getJobId, jobId)
                .select(TDJob::getJobId, TDJob::getJobArea, TDJob::getJobCategory, TDJob::getJobContact
                        , TDJob::getJobContactsPhone, TDJob::getJobEducation, TDJob::getJobExperience,
                        TDJob::getJobNature, TDJob::getJobPersonNumber, TDJob::getJobPositionDescribe,
                        TDJob::getJobSalary, TDJob::getJobTime, TDJob::getJobPosition);

        TDJob tdJob = tdJobMapper.selectOne(jobInfo);
        JobDetailVo jobDetailVo = new JobDetailVo();
        BeanUtils.copyProperties(tdJob, jobDetailVo);
        return jobDetailVo;
    }

    @Override
    public JobDetailVo updateJob(JobDetailVo jobDetailVo) {
        TDJob tdJob = new TDJob();
        BeanUtils.copyProperties(jobDetailVo, tdJob);
        UpdateWrapper<TDJob> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("job_id", tdJob.getJobId())
                .set("job_review", Common.REVIEW_WAIT.getCode())
                .set("job_status", Common.STATUS_CLOSE.getCode());

        if (tdJobMapper.update(tdJob, updateWrapper) < 1) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        return null;
    }

    @Override
    public JobDetailVo insertJobInfo(JobInsertVo jobInsertVo, Long companyId) {
        TDJob tdJob = new TDJob();
        BeanUtils.copyProperties(jobInsertVo, tdJob);
        tdJob.setCompanyId(companyId);
        tdJob.setJobReview(Common.REVIEW_WAIT.getCode());
        tdJob.setJobStatus(Common.STATUS_CLOSE.getCode());
        tdJob.setJobTime(new Date());

        if (tdJobMapper.insert(tdJob) < 1) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        return null;
    }

    @Override
    public Object deleteJob(Long jobId) {
        if (tdJobMapper.deleteById(jobId)<1) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }

        return null;
    }


}


