package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.TDCompanyInfo;
import com.queryx.recruiting_website.domain.TDJob;
import com.queryx.recruiting_website.domain.TPMenu;
import com.queryx.recruiting_website.domain.dto.JobDTO;
import com.queryx.recruiting_website.exception.SystemException;
import com.queryx.recruiting_website.mapper.TDCompanyInfoMapper;
import com.queryx.recruiting_website.mapper.TDJobMapper;
import com.queryx.recruiting_website.service.TDCompanyInfoService;
import com.queryx.recruiting_website.service.TDJobService;
import com.queryx.recruiting_website.domain.vo.JobCompanyListVO;
import com.queryx.recruiting_website.domain.dto.JobDetailDTO;
import com.queryx.recruiting_website.domain.dto.JobInsertDTO;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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
    public IPage<JobCompanyListVO> selectJobList(Integer page, Integer size, Long companyId, String jobName, String jobReview, String status) {

        LambdaQueryWrapper<TDJob> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(jobName != null, TDJob::getJobPosition, jobName)
                .eq(status != null, TDJob::getJobStatus, status)
                .eq(jobReview != null, TDJob::getJobReview, jobReview)
                .eq(companyId != null, TDJob::getCompanyId, companyId);

        Page<TDJob> pageVO = new Page<>(page, size);
        IPage<TDJob> jobPage = tdJobMapper.selectPage(pageVO, wrapper);

        if (jobPage.getRecords().isEmpty()) {
            return null;
        }
        // 根据公司id进行分组
        List<Long> companyIds = jobPage.getRecords().stream().map(TDJob::getCompanyId).toList();
        List<TDCompanyInfo> tdCompanyInfos = companyInfoService.listByIds(companyIds);
        Map<Long, String> companyNames = tdCompanyInfos.stream()
                .collect(Collectors.toMap(TDCompanyInfo::getCompanyInfoId, TDCompanyInfo::getCompanyInfoName));
        // 进行类型转换并把每个工作得公司名set
        IPage<JobCompanyListVO> jobCompanyListVOPage = new Page<>(jobPage.getCurrent(), jobPage.getSize(), jobPage.getTotal());
        jobCompanyListVOPage.setRecords(jobPage.getRecords().stream().map(tdJob -> {
            JobCompanyListVO jobCompanyListVO = new JobCompanyListVO();
            BeanUtils.copyProperties(tdJob, jobCompanyListVO);
            jobCompanyListVO.setCompanyName(companyNames.get(tdJob.getCompanyId()));
            return jobCompanyListVO;
        }).collect(Collectors.toList()));

        return jobCompanyListVOPage;
    }

    @Override
    public JobDetailDTO selectJobInfo(Long jobId) {
        LambdaQueryWrapper<TDJob> jobInfo = new LambdaQueryWrapper<TDJob>().eq(TDJob::getJobId, jobId)
                .select(TDJob::getJobId, TDJob::getJobArea, TDJob::getJobCategory, TDJob::getJobContact
                        , TDJob::getJobContactsPhone, TDJob::getJobEducation, TDJob::getJobExperience,
                        TDJob::getJobNature, TDJob::getJobPersonNumber, TDJob::getJobPositionDescribe,
                        TDJob::getJobSalary, TDJob::getJobTime, TDJob::getJobPosition);

        TDJob tdJob = tdJobMapper.selectOne(jobInfo);
        String companyName = companyInfoService.getById(tdJob.getCompanyId()).getCompanyInfoName();
        JobDetailDTO jobDetailDTO = new JobDetailDTO();
        BeanUtils.copyProperties(tdJob, jobDetailDTO);
        jobDetailDTO.setCompanyName(companyName);
        return jobDetailDTO;
    }

    @Override
    public JobDetailDTO updateJob(JobDetailDTO jobDetailDTO) {
        TDJob tdJob = new TDJob();
        BeanUtils.copyProperties(jobDetailDTO, tdJob);
        UpdateWrapper<TDJob> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("job_id", tdJob.getJobId())
                .set("job_review", Common.REVIEW_WAIT)
                .set("job_status", Common.STATUS_CLOSE);
        if (jobDetailDTO.getCompanyId() != null) {
            updateWrapper.set("company_id", jobDetailDTO.getCompanyId());
        }

        if (tdJobMapper.update(tdJob, updateWrapper) < 1) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        return null;
    }

    @Override
    public JobDetailDTO insertJobInfo(JobInsertDTO jobInsertDTO, Long companyId) {
        TDJob tdJob = new TDJob();
        BeanUtils.copyProperties(jobInsertDTO, tdJob);
        tdJob.setCompanyId(companyId);
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
        if (tdJobMapper.deleteById(jobId) < 1) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }

        return null;
    }

    @Override
    public Object addJob(JobDTO jobDTO) {
        TDJob tdJob = new TDJob();
        BeanUtils.copyProperties(jobDTO, tdJob);
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


}


