package io.kshired.multilayer.cache.example.controller

import io.kshired.multilayer.cache.example.service.CacheService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class CacheController(
    private val cacheService: CacheService
) {
    @GetMapping("/local")
    fun getLocalCache(
        @RequestParam key: String
    ): String {
        return cacheService.getLocalCache(key)
    }

    @GetMapping("/redis")
    fun getRedisCache(
        @RequestParam key: String
    ): String {
        return cacheService.getRedisCache(key)
    }

    @GetMapping("/multi")
    fun getMultiLayerCache(
        @RequestParam key: String
    ): String {
        return cacheService.getMultiLayerCache(key)
    }
}
