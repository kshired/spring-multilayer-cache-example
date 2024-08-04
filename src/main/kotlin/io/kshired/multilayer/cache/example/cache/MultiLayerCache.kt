package io.kshired.multilayer.cache.example.cache

import org.springframework.cache.Cache
import org.springframework.cache.support.SimpleValueWrapper
import java.util.concurrent.Callable

class MultiLayerCache(
    private val caches: List<Cache>
) : Cache {
    override fun getName(): String {
        return caches.first().name
    }

    override fun getNativeCache(): Any {
        return this
    }

    override fun get(key: Any): Cache.ValueWrapper? {
        for ((idx, cache) in caches.withIndex()) {
            cache[key]?.get()?.let {
                cacheToLowerLevel(idx, key, it)
                return SimpleValueWrapper(it)
            }
        }

        return null
    }

    override fun <T : Any?> get(key: Any, type: Class<T>?): T? {
        for ((idx, cache) in caches.withIndex()) {
            cache[key, type]?.let {
                cacheToLowerLevel(idx, key, it)
                return it
            }
        }

        return null
    }

    override fun <T : Any?> get(key: Any, valueLoader: Callable<T>): T? {
        for ((idx, cache) in caches.withIndex()) {
            cache[key, valueLoader]?.let {
                cacheToLowerLevel(idx, key, it)
                return it
            }
        }

        return null
    }

    override fun put(key: Any, value: Any?) {
        caches.reversed().forEach {
            it.put(key, value)
        }
    }

    override fun putIfAbsent(key: Any, value: Any?): Cache.ValueWrapper {
        caches.reversed().forEach {
            it.putIfAbsent(key, value)
        }
        return SimpleValueWrapper(value)
    }


    override fun evict(key: Any) {
        caches.reversed().forEach {
            it.evict(key)
        }
    }

    override fun clear() {
        caches.reversed().forEach {
            it.clear()
        }
    }

    private fun cacheToLowerLevel(idx: Int, key: Any, value: Any?) {
        for (i in idx - 1 downTo 0) {
            caches[i].put(key, value)
        }
    }
}
