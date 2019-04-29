package com.ajeyone.lrudemo.data;

public class MemoryCacheConfig {
    public static long getCacheMaxSize() {
        long bytes = Runtime.getRuntime().maxMemory();
        return bytes / 8;
    }
}
