package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TDMaterial;

public interface MaterialService extends IService<TDMaterial> {


    Object selectMaterial(Integer page, Integer size,Long jobId);

    Object selectMaterialDetail(Long materialId, Long offerId);

    Object updateMaterialStatus(Long materialId, String status);
}
