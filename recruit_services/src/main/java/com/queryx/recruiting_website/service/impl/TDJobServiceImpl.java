package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.TDCompanyInfo;
import com.queryx.recruiting_website.domain.TDJob;
import com.queryx.recruiting_website.domain.dto.JobDetailDto;
import com.queryx.recruiting_website.domain.dto.JobDto;
import com.queryx.recruiting_website.domain.dto.JobInsertDto;
import com.queryx.recruiting_website.domain.vo.JobCompanyListVO;
import com.queryx.recruiting_website.exception.SystemException;
import com.queryx.recruiting_website.mapper.TDJobMapper;
import com.queryx.recruiting_website.service.TDCompanyInfoService;
import com.queryx.recruiting_website.service.TDJobService;
import com.queryx.recruiting_website.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("tDJobService")
public class TDJobServiceImpl extends ServiceImpl<TDJobMapper, TDJob> implements TDJobService {
    @Resource
    private TDJobMapper tdJobMapper;

    @Resource
    private TDCompanyInfoService companyInfoService;


    @Override
    public IPage<JobCompanyListVO> selectJobList(Integer page, Integer size, String companyName,
                                                 String jobName, String jobReview, String status, String jobCategory, String jobNature) {
        List<Long> targetCompanyIds = null;

        LambdaQueryWrapper<TDCompanyInfo> companyQueryWrapper = new LambdaQueryWrapper<>();
        companyQueryWrapper.like(StringUtils.hasText(companyName), TDCompanyInfo::getCompanyInfoName, companyName)
                .eq(TDCompanyInfo::getCompanyInfoStatus, Common.STATUS_ENABLE);
        List<TDCompanyInfo> companies = companyInfoService.list(companyQueryWrapper);
        if (companies.isEmpty()) {
            return new Page<>(page, size);
        }
        targetCompanyIds = companies.stream()
                .map(TDCompanyInfo::getCompanyInfoId)
                .collect(Collectors.toList());


        LambdaQueryWrapper<TDJob> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(jobName), TDJob::getJobPosition, jobName)
                .eq(StringUtils.hasText(status), TDJob::getJobStatus, status)
                .eq(StringUtils.hasText(jobReview), TDJob::getJobReview, jobReview)
                .like(StringUtils.hasText(jobCategory), TDJob::getJobCategory, jobCategory)
                .eq(TDJob::getDelFlag, Common.NOT_DELETE)
                .like(StringUtils.hasText(jobNature), TDJob::getJobNature, jobNature)
                .in(TDJob::getCompanyId, targetCompanyIds);

        Page<TDJob> pageVO = new Page<>(page, size);
        IPage<TDJob> jobPage = tdJobMapper.selectPage(pageVO, wrapper);
        if (jobPage.getRecords().isEmpty()) {
            return new Page<>(page, size);
        }


        List<Long> companyIds = jobPage.getRecords().stream()
                .map(TDJob::getCompanyId)
                .distinct()
                .collect(Collectors.toList());
        List<TDCompanyInfo> tdCompanyInfos = companyInfoService.listByIds(companyIds);
        Map<Long, String> companyNames = tdCompanyInfos.stream()
                .collect(Collectors.toMap(
                        TDCompanyInfo::getCompanyInfoId,
                        TDCompanyInfo::getCompanyInfoName,
                        (k1, k2) -> k1));
        IPage<JobCompanyListVO> resultPage = new Page<>(
                jobPage.getCurrent(),
                jobPage.getSize(),
                jobPage.getTotal()
        );

        resultPage.setRecords(jobPage.getRecords().stream().map(tdJob -> {
            JobCompanyListVO vo = new JobCompanyListVO();
            BeanUtils.copyProperties(tdJob, vo);
            vo.setCompanyName(companyNames.get(tdJob.getCompanyId()));
            return vo;
        }).collect(Collectors.toList()));

        return resultPage;
    }

    @Override
    public JobDetailDto selectJobInfo(Long jobId) {
        LambdaQueryWrapper<TDJob> jobInfo = new LambdaQueryWrapper<TDJob>().eq(TDJob::getJobId, jobId)
                .select(TDJob::getJobId, TDJob::getJobArea, TDJob::getJobCategory, TDJob::getJobContact
                        , TDJob::getJobEducation, TDJob::getJobExperience,
                        TDJob::getJobNature, TDJob::getJobPersonNumber, TDJob::getJobPositionDescribe,
                        TDJob::getJobSalary, TDJob::getJobTime, TDJob::getJobPosition);

        TDJob tdJob = tdJobMapper.selectOne(jobInfo);
        String companyName = companyInfoService.getById(tdJob.getCompanyId()).getCompanyInfoName();
        JobDetailDto jobDetailDto = new JobDetailDto();
        BeanUtils.copyProperties(tdJob, jobDetailDto);
        jobDetailDto.setCompanyName(companyName);
        return jobDetailDto;
    }

    @Override
    public JobDetailDto updateJob(JobDetailDto jobDetailDto) {
        TDJob tdJob = new TDJob();
        BeanUtils.copyProperties(jobDetailDto, tdJob);

        tdJobMapper.update(tdJob, new LambdaUpdateWrapper<TDJob>().eq(TDJob::getJobId, jobDetailDto.getJobId()));
        return null;
    }

    @Override
    public JobDetailDto insertJobInfo(JobInsertDto jobInsertDto) {
        TDJob tdJob = new TDJob();
        BeanUtils.copyProperties(jobInsertDto, tdJob);
        tdJob.setCompanyId(SecurityUtils.getLoginUser().getTdUser().getCompanyInfoId());
        tdJob.setJobReview(Common.REVIEW_WAIT);
        tdJob.setJobStatus(Common.STATUS_CLOSE);
        tdJob.setJobTime(new Date());

        if (tdJobMapper.insert(tdJob) < 1) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        return null;
    }

    @Override
    public Object deleteJob(Long jobId) {

        if (tdJobMapper.update(new LambdaUpdateWrapper<TDJob>().eq(TDJob::getJobId, jobId)
                .set(TDJob::getDelFlag, Common.DELETE)) < 1) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }

        return null;
    }

    @Override
    public Object addJob(JobDto jobDto) {
        TDJob tdJob = new TDJob();
        BeanUtils.copyProperties(jobDto, tdJob);
        tdJob.setJobReview(Common.REVIEW_OK);
        tdJob.setJobStatus(Common.STATUS_PUBLISH);
        tdJob.setJobTime(new Date());

        if (tdJobMapper.insert(tdJob) < 1) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        return null;
    }

    @Override
    public Object jobReview(String review, Long jobId) {
        LambdaUpdateWrapper<TDJob> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(TDJob::getJobId, jobId)
                .set(TDJob::getJobReview, review)
                .set(TDJob::getJobStatus, Common.STATUS_PUBLISH);
        if (!update(updateWrapper)) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        return null;
    }

    @Override
    public Object selectCompanyJobList(Integer page, Integer size, String jobName, String jobReview, String
            jobCategory) {
        Long companyInfoId = SecurityUtils.getLoginUser().getTdUser().getCompanyInfoId();
        LambdaQueryWrapper<TDJob> tdJobLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tdJobLambdaQueryWrapper.eq(TDJob::getCompanyId, companyInfoId)
                .like(StringUtils.hasText(jobName), TDJob::getJobPosition, jobName)
                .eq(StringUtils.hasText(jobReview), TDJob::getJobReview, jobReview)
                .eq(TDJob::getDelFlag, Common.NOT_DELETE)
                .like(StringUtils.hasText(jobCategory), TDJob::getJobCategory, jobCategory);
        Page<TDJob> tdJobPage = tdJobMapper.selectPage(new Page<>(page, size), tdJobLambdaQueryWrapper);
        if (tdJobPage.getRecords().isEmpty()) {
            return null;
        }
        Page<JobCompanyListVO> jobCompanyListVOPage = new Page<>(tdJobPage.getCurrent(), tdJobPage.getSize(), tdJobPage.getTotal());
        jobCompanyListVOPage.setRecords(tdJobPage.getRecords().stream().map(job -> {
            JobCompanyListVO jobCompanyListVO = new JobCompanyListVO();
            BeanUtils.copyProperties(job, jobCompanyListVO);
            return jobCompanyListVO;
        }).toList());
        return jobCompanyListVOPage;
    }


}


