package com.example.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.util.SerializeUtil;
import com.example.util.TypeUtil;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;

@EnableCaching
@Configuration
@EnableConfigurationProperties(CacheProperties.class)
public class RedisCacheConfig {

    @Bean
    public RedisSerializer<String> stringRedisSerializer() {
        return new StringRedisSerializer();
    }

    @Bean
    public RedisSerializer<Object> objectRedisSerializer() {
        return new RedisSerializer<Object>() {
            @Override
            public byte[] serialize(Object obj) throws SerializationException {
                return SerializeUtil.serialize(obj);
            }

            @Override
            public Object deserialize(byte[] bytes) throws SerializationException {
                return SerializeUtil.deserialize(bytes);
            }
        };
    }

    // 配置 redisTemplate
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            LettuceConnectionFactory connectionFactory,
            RedisSerializer<String> stringRedisSerializer,
            RedisSerializer<Object> objectRedisSerializer) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        // key序列化
        redisTemplate.setKeySerializer(stringRedisSerializer);
        // value序列化
        redisTemplate.setValueSerializer(objectRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory connectionFactory,
            CacheProperties cacheProperties,
            RedisSerializer<String> stringRedisSerializer,
            RedisSerializer<Object> objectRedisSerializer) {
        // 配置序列化
        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(stringRedisSerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(objectRedisSerializer))
                .entryTtl(redisProperties.getTimeToLive())
                .computePrefixWith(cacheName -> redisProperties.getKeyPrefix()+":"+cacheName+":");
        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }

        return RedisCacheManager
                .builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }

    @Bean
    public KeyGenerator defaultPageKeyGenerator() {
        return new KeyGenerator() {
            private static final String SEPARATOR = ":";
            private static final String NO_PARAM_KEY = "noparam";

            private String toJson(Object obj) {
                return JSON.toJSONString(obj, SerializerFeature.WriteMapNullValue);
            }

            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder builder = new StringBuilder();
                builder.append(target.getClass().getName());
                builder.append(SEPARATOR);
                builder.append(method.getName());
                builder.append(SEPARATOR);
                if (params.length>0) {
                    // 参数值
                    for (Object object : params) {
                        if (TypeUtil.isSimpleValueType(object.getClass())) {
                            builder.append(object);
                        } else {
                            builder.append(toJson(object).hashCode());
                        }
                    }
                } else {
                    builder.append(NO_PARAM_KEY);
                }
                return builder.toString();
            }
        };
    }

}
