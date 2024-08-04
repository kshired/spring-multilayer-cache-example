package io.kshired.multilayer.cache.example.cache.layer

import java.time.Duration

sealed class CacheInfo(
    val cacheName: String,
    val ttl: Duration
) {
    class LocalCacheInfo(
        cacheName: String,
        val maxCacheItem: Long,
        ttl: Duration,
    ) : CacheInfo(cacheName, ttl)

    class RedisCacheInfo(
        cacheName: String,
        ttl: Duration
    ) : CacheInfo(cacheName, ttl)
}
