package io.kshired.multilayer.cache.example.cache.layer

import java.time.Duration

const val LOCAL_SAMPLE_CACHE_NAME = "local_sample_cache"

enum class LocalLayer(
    private val cacheName: String,
    private val maxCacheItem: Long,
    private val cacheTtl: Duration
) {
    LOCAL_SAMPLE(LOCAL_SAMPLE_CACHE_NAME, 1000, Duration.ofMinutes(10)),
    ;
    fun toCacheInfo(): CacheInfo.LocalCacheInfo = CacheInfo.LocalCacheInfo(cacheName, maxCacheItem, cacheTtl)
}
