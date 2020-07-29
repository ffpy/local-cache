package com.example.localcache.vo;

import com.example.localcache.util.NumberUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.concurrent.TimeUnit;

/**
 * 本地缓存统计信息
 *
 * @author 温龙盛
 * @date 2020/7/16 17:23
 */
@Getter
@Setter
@ToString
public class LocalCacheStats {

    @JsonProperty(value = "缓存大小", index = 0)
    private final long size;

    @JsonProperty(value = "请求次数", index = 1)
    private final long requestCount;

    @JsonProperty(value = "命中次数", index = 2)
    private final long hitCount;

    @JsonProperty(value = "命中率", index = 3)
    private final String hitRate;

    @JsonProperty(value = "丢失次数", index = 4)
    private final long missCount;

    @JsonProperty(value = "丢失率", index = 5)
    private final String missRate;

    @JsonProperty(value = "加载次数", index = 6)
    private final long loadCount;

    @JsonProperty(value = "加载成功次数", index = 7)
    private final long loadSuccessCount;

    @JsonProperty(value = "加载失败次数", index = 8)
    private final long loadFailureCount;

    @JsonProperty(value = "加载总耗时(毫秒)", index = 9)
    private final long totalLoadTime;

    @JsonProperty(value = "加载平均耗时(毫秒)", index = 10)
    private final long averageLoadTime;

    @JsonProperty(value = "过期次数", index = 11)
    private final long evictionCount;

    /**
     * @param size  缓存大小
     * @param stats 缓存统计信息
     */
    public LocalCacheStats(long size, CacheStats stats) {
        this.size = size;
        this.requestCount = stats.requestCount();
        this.hitCount = stats.hitCount();
        this.hitRate = NumberUtils.toPercentString(stats.hitRate());
        this.missCount = stats.missCount();
        this.missRate = NumberUtils.toPercentString(stats.missRate());
        this.loadCount = stats.loadCount();
        this.loadSuccessCount = stats.loadSuccessCount();
        this.loadFailureCount = stats.loadFailureCount();
        this.totalLoadTime = TimeUnit.NANOSECONDS.toMillis(stats.totalLoadTime());
        this.averageLoadTime = TimeUnit.NANOSECONDS.toMillis((long) stats.averageLoadPenalty());
        this.evictionCount = stats.evictionCount();
    }
}
