package io.kshired.multilayer.cache.example.cache.manager

import io.kshired.multilayer.cache.example.cache.MultiLayerCache
import org.springframework.beans.factory.InitializingBean
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.cache.support.AbstractCacheManager
import java.util.concurrent.ConcurrentHashMap

class MultiLayerCacheManager(
    private val cacheManagers: List<AbstractCacheManager>,
    private val cacheMap: MutableMap<String, Cache> = ConcurrentHashMap()
) : CacheManager, InitializingBean {
    override fun getCache(name: String): Cache {
        return cacheMap.computeIfAbsent(name) {
            MultiLayerCache(getCaches(it))
        }
    }

    private fun getCaches(name: String): List<Cache> {
        return cacheManagers.mapNotNull { it.getCache(name) }
    }

    override fun getCacheNames(): Collection<String> {
        return cacheManagers.flatMap { it.cacheNames }
    }

    override fun afterPropertiesSet() {
        cacheManagers.forEach {
            it.initializeCaches()
        }
    }
}
