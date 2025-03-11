package com.queryx.recruiting_website.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.queryx.recruiting_website.domain.TDMaterial;
import com.queryx.recruiting_website.domain.dto.MaterialDTO;

/**
 * @Author：fjj
 * @Date：2025/3/10 9:59
 */
public interface MaterialsMapper extends BaseMapper<TDMaterial> {

    MaterialDTO selectOfferAndJobId(Long userId, String offerStatus);

}

