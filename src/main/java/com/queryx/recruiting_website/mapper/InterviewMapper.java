package com.queryx.recruiting_website.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.queryx.recruiting_website.domain.TDInterview;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface InterviewMapper extends BaseMapper<TDInterview> {

    @Select("SELECT " +
            "i.interview_job, " +
            "i.interview_result, " +
            "i.interview_type, " +
            "i.interview_date, " +
            "i.interview_region, " +
            "u.user_name, " +
            "c.company_info_name " +
            "FROM t_d_interview i " +
            "INNER JOIN t_d_user u ON " +
//            "INNER JOIN t_d_user u ON u.user_id = #{userId} " +
            "INNER JOIN t_d_company_info c ON i.company_id = c.company_info_id " +
            "WHERE i.user_id = #{userId} AND i.is_deleted = 0;")
    List<TDInterview> getInterviewsByUserId(Long userId);
}
