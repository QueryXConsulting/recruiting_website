package com.queryx.recruiting_website.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 清除缓存定时任务类
 *
 * @Author：fjj
 * @Date：2025/3/27 9:15
 */
@Slf4j
@Component
public class CacheEvictionTask {

    @Autowired
    private CacheManager cacheManager;

    // 每隔5分钟清除一次缓存
//    @Scheduled(fixedRate = 30000)
//    public void evictAllCaches() {
//        for (String cacheName : cacheManager.getCacheNames()) {
//            Cache cache = cacheManager.getCache(cacheName);
//            System.err.println("==================================清除缓存：" + cacheName);
//            if (cache != null) {
//                cache.clear(); // 清除缓存
//                System.err.println("==================================缓存已清除：" + cacheName);
//            }
//        }
//    }


    // 清除职位列表缓存
    @Scheduled(fixedRate = 300000) // 每隔5分钟清除一次缓存
    public void evictJobsCache() {
        Cache jobsCache = cacheManager.getCache("jobList");
        if (jobsCache == null) {
            return;
        }
        jobsCache.clear(); // 清除缓存
        log.info("缓存已清除：jobList");
    }


    // 清除公司列表缓存
    @Scheduled(fixedRate = 300000) // 每隔5分钟清除一次缓存
    public void evictCompanyListCache() {
        Cache cache = cacheManager.getCache("companyList");
        if (cache == null) {
            return;
        }
        cache.clear(); // 清除缓存
        log.info("缓存已清除：companyList");
    }

    // 清除职位类型缓存
    @Scheduled(fixedRate = 300000) // 每隔5分钟清除一次缓存
    public void evictJobNaturesCache() {
        Cache cache = cacheManager.getCache("jobNatureList");
        if (cache == null) {
            return;
        }
        cache.clear(); // 清除缓存
        log.info("缓存已清除：jobNatureList");
    }
}
