package io.kshired.multilayer.cache.example.cache.config

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.benmanes.caffeine.cache.Caffeine
import io.kshired.multilayer.cache.example.cache.MultiLayerCache
import io.kshired.multilayer.cache.example.cache.layer.CacheInfo
import io.kshired.multilayer.cache.example.cache.layer.LocalLayer
import io.kshired.multilayer.cache.example.cache.layer.MultiLayer
import io.kshired.multilayer.cache.example.cache.layer.RedisLayer
import io.kshired.multilayer.cache.example.cache.manager.MultiLayerCacheManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.metrics.cache.CacheMeterBinderProvider
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.cache.support.CompositeCacheManager
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@EnableCaching
@Configuration
class CacheConfig(
    @Value("\${spring.application.name}")
    private val applicationName: String,
    private val redisConnectionFactory: RedisConnectionFactory,
) {
    @Bean(name = [CacheConstants.REDIS_LAYER_CACHE_MANAGER])
    fun redisLayerCacheManager(): CacheManager {
        return redisLayerCacheManager(RedisLayer.entries.map { it.toCacheInfo() })
    }

    @Bean(name = [CacheConstants.LOCAL_LAYER_CACHE_MANAGER])
    fun localLayerCacheManager(): CacheManager {
        return localLayerCacheManager(LocalLayer.entries.map { it.toCacheInfo() })
    }

    @Primary
    @Bean(name = [CacheConstants.COMPOSITE_CACHE_MANAGER])
    fun compositeCacheManager(): CacheManager {
        return CompositeCacheManager(
            localLayerCacheManager(),
            redisLayerCacheManager(),
        )
    }

    @Bean(name = [CacheConstants.MULTI_LAYER_CACHE_MANAGER])
    fun multiLayerCacheManager(): CacheManager {
        return MultiLayerCacheManager(
            listOf(
                localLayerCacheManager(MultiLayer.entries.map { it.toLocalCacheInfo() }),
                redisLayerCacheManager(MultiLayer.entries.map { it.toRedisCacheInfo() }),
            )
        )
    }

    @Bean
    fun multiLayerCacheMeterBinderProvider(): CacheMeterBinderProvider<MultiLayerCache> {
        return CacheMeterBinderProvider<MultiLayerCache> { cache, _ ->
            cache.getMeterBinder()
        }
    }

    private fun redisLayerCacheManager(redisCacheInfos: List<CacheInfo.RedisCacheInfo>): RedisCacheManager {
        val objectMapper = jacksonObjectMapper()
            .registerKotlinModule()
            .registerModule(JavaTimeModule())
            .activateDefaultTyping(
                jacksonObjectMapper().polymorphicTypeValidator,
                ObjectMapper.DefaultTyping.EVERYTHING,
                JsonTypeInfo.As.PROPERTY
            )


        val redisCacheConfigurationMap = redisCacheInfos.associate {
            it.cacheName to RedisCacheConfiguration.defaultCacheConfig()
                .prefixCacheNameWith("$applicationName:")
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer()))
                .serializeValuesWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(
                        GenericJackson2JsonRedisSerializer(objectMapper)
                    )
                )
                .entryTtl(it.ttl)
        }

        return RedisCacheManager.RedisCacheManagerBuilder
            .fromConnectionFactory(redisConnectionFactory)
            .withInitialCacheConfigurations(redisCacheConfigurationMap)
            .disableCreateOnMissingCache()
            .enableStatistics()
            .build()
    }

    private fun localLayerCacheManager(localCacheInfos: List<CacheInfo.LocalCacheInfo>): SimpleCacheManager {
        val cacheManger = SimpleCacheManager()
        cacheManger.setCaches(
            localCacheInfos.map {
                CaffeineCache(
                    it.cacheName,
                    Caffeine.newBuilder()
                        .maximumSize(it.maxCacheItem)
                        .expireAfterWrite(it.ttl)
                        .recordStats()
                        .build()
                )
            }
        )
        return cacheManger
    }
}
