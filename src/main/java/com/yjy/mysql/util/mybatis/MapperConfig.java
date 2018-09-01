package com.yjy.mysql.util.mybatis;

import java.util.Map;

/**
 * @author yjy
 * 2018-08-06 16:16
 */
public class MapperConfig {

    private String entityPackage; // 实体类所在包
    private String mapperPackage; // mapper接口所在包
    private String xmlOutPut; // xml文件输出位置
    private boolean useCache; // 是否启用二级缓存
    private long cacheTime; // 二级缓存有效期, 默认为0表示不开启, 单位为毫秒
    private String cacheClass; // 自定义二级缓存实现类, 默认使用Mybatis自带缓存类, 如需分布式缓存,则需自己实现
    private Map<String, String> customCachePros; // 自定义缓存类的属性配置
    private int maxLimit; // 未指定limit时, 是否限制最大获取条数, 0|-1 为不限制

    public MapperConfig(String entityPackage, String mapperPackage, String xmlOutPut) {
        this(entityPackage, mapperPackage, xmlOutPut, true, 0, null, -1, null);
    }

    public MapperConfig(String entityPackage, String mapperPackage, String xmlOutPut, boolean useCache) {
        this(entityPackage, mapperPackage, xmlOutPut, useCache, 0, null, -1, null);
    }

    public MapperConfig(String entityPackage, String mapperPackage, String xmlOutPut, long cacheTime) {
        this(entityPackage, mapperPackage, xmlOutPut, true, cacheTime, null, -1, null);
    }

    public MapperConfig(String entityPackage, String mapperPackage, String xmlOutPut, String cacheClass) {
        this(entityPackage, mapperPackage, xmlOutPut, true, 0, cacheClass, -1, null);
    }

    public MapperConfig(String entityPackage, String mapperPackage, String xmlOutPut, String cacheClass, Map<String, String> customCachePros) {
        this(entityPackage, mapperPackage, xmlOutPut, true, 0, cacheClass, -1, customCachePros);
    }

    public MapperConfig(String entityPackage, String mapperPackage, String xmlOutPut, boolean useCache, long cacheTime) {
        this(entityPackage, mapperPackage, xmlOutPut, useCache, cacheTime, null, -1, null);
    }

    public MapperConfig(String entityPackage, String mapperPackage, String xmlOutPut, boolean useCache,
                        long cacheTime, String cacheClass, int maxLimit) {
        this(entityPackage, mapperPackage, xmlOutPut, useCache, cacheTime, cacheClass, maxLimit, null);
    }

    public MapperConfig(String entityPackage, String mapperPackage, String xmlOutPut, boolean useCache,
                        long cacheTime, String cacheClass, int maxLimit, Map<String, String> customCachePros) {
        this.entityPackage = entityPackage;
        this.mapperPackage = mapperPackage;
        this.xmlOutPut = xmlOutPut;
        this.useCache = useCache;
        this.cacheTime = cacheTime;
        this.cacheClass = cacheClass;
        this.maxLimit = maxLimit;
        this.customCachePros = customCachePros;
    }

    public String getEntityPackage() {
        return entityPackage;
    }

    public void setEntityPackage(String entityPackage) {
        this.entityPackage = entityPackage;
    }

    public String getMapperPackage() {
        return mapperPackage;
    }

    public void setMapperPackage(String mapperPackage) {
        this.mapperPackage = mapperPackage;
    }

    public String getXmlOutPut() {
        return xmlOutPut;
    }

    public void setXmlOutPut(String xmlOutPut) {
        this.xmlOutPut = xmlOutPut;
    }

    public boolean isUseCache() {
        return useCache;
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    public long getCacheTime() {
        return cacheTime;
    }

    public void setCacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
    }

    public String getCacheClass() {
        return cacheClass;
    }

    public void setCacheClass(String cacheClass) {
        this.cacheClass = cacheClass;
    }

    public int getMaxLimit() {
        return maxLimit;
    }

    public void setMaxLimit(int maxLimit) {
        this.maxLimit = maxLimit;
    }

    public Map<String, String> getCustomCachePros() {
        return customCachePros;
    }

    public void setCustomCachePros(Map<String, String> customCachePros) {
        this.customCachePros = customCachePros;
    }
}
