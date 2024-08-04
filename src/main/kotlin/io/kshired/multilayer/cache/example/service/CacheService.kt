package io.kshired.multilayer.cache.example.service

import io.kshired.multilayer.cache.example.cache.config.CacheConstants.MULTI_LAYER_CACHE_MANAGER
import io.kshired.multilayer.cache.example.cache.layer.LOCAL_SAMPLE_CACHE_NAME
import io.kshired.multilayer.cache.example.cache.layer.MULTI_SAMPLE_CACHE_NAME
import io.kshired.multilayer.cache.example.cache.layer.REDIS_SAMPLE_CACHE_NAME
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class CacheService {
    @Cacheable(
        cacheNames = [LOCAL_SAMPLE_CACHE_NAME],
        key = "#key",
    )
    fun getLocalCache(key: String): String {
        return "local cache value for $key"
    }

    @Cacheable(
        cacheNames = [REDIS_SAMPLE_CACHE_NAME],
        key = "#key",
    )
    fun getRedisCache(key: String): String {
        return "redis cache value for $key"
    }

    @Cacheable(
        cacheNames = [MULTI_SAMPLE_CACHE_NAME],
        cacheManager = MULTI_LAYER_CACHE_MANAGER,
        key = "#key",
    )
    fun getMultiLayerCache(key: String): String {
        return "multi-layer cache value for $key"
    }
}
