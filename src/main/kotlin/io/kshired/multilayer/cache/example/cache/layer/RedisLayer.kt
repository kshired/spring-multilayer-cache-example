package io.kshired.multilayer.cache.example.cache.layer

import java.time.Duration

const val REDIS_SAMPLE_CACHE_NAME = "redis_sample_cache"

enum class RedisLayer(
    private val cacheName: String,
    private val cacheTtl: Duration
) {
    REDIS_SAMPLE(REDIS_SAMPLE_CACHE_NAME, Duration.ofHours(2))
    ;

    fun toCacheInfo(): CacheInfo.RedisCacheInfo = CacheInfo.RedisCacheInfo(cacheName, cacheTtl)
}
