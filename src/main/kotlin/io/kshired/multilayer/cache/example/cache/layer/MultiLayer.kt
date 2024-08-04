package io.kshired.multilayer.cache.example.cache.layer

import java.time.Duration

const val MULTI_SAMPLE_CACHE_NAME = "multi_sample_cache"

enum class MultiLayer(
    private val cacheName: String,
    private val maxCacheItem: Long,
    private val localCacheTtl: Duration,
    private val redisCacheTtl: Duration
) {
    MULTI_SAMPLE(MULTI_SAMPLE_CACHE_NAME, 1000, Duration.ofMinutes(10), Duration.ofHours(3))
    ;

    fun toLocalCacheInfo(): CacheInfo.LocalCacheInfo = CacheInfo.LocalCacheInfo(cacheName, maxCacheItem, localCacheTtl)
    fun toRedisCacheInfo(): CacheInfo.RedisCacheInfo = CacheInfo.RedisCacheInfo(cacheName, redisCacheTtl)
}
