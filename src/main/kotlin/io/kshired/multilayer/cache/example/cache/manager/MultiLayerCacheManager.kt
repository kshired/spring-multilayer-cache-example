package io.kshired.multilayer.cache.example.cache.manager

import io.kshired.multilayer.cache.example.cache.MultiLayerCache
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.cache.support.AbstractCacheManager
import java.util.concurrent.ConcurrentHashMap

class MultiLayerCacheManager(
    vararg cacheManagers: CacheManager,
    private val cacheMap: MutableMap<String, Cache> = ConcurrentHashMap()
) : AbstractCacheManager() {
    private val cacheManagers: List<CacheManager> = cacheManagers.toList()

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

    /**
     * no-op : cacheManger들의 초기화는 initializeCaches에서 수행
     */
    override fun loadCaches(): MutableCollection<out Cache> {
        return mutableSetOf()
    }

    /**
     * abstractCacheManager를 상속한 cacheManager들의 initializeCaches를 호출
     * 위 메서드를 호출하지 않으면 abstractCacheManager를 상속한 cacheManager 들의 cache가 제대로 초기화 되지 않음
     */
    override fun initializeCaches() {
        cacheManagers.forEach {
            when (it) {
                is AbstractCacheManager -> it.initializeCaches()
            }
        }
    }
}
