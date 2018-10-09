package com.zoi7.mysql.example.mybatis;

import com.zoi7.mysql.util.mybatis.MapperConfig;

/**
 *
 * 扩展 MapperConfig 配置
 * @author yjy
 * 2018-08-14 13:22
 */
public class CustomMapperConfig extends MapperConfig {

    private boolean createDelete = true; // 是否生成 delete sql

    public CustomMapperConfig(String entityPackage, String mapperPackage, String xmlOutPut) {
        super(entityPackage, mapperPackage, xmlOutPut, true, 0, null, -1);
    }

    public CustomMapperConfig(String entityPackage, String mapperPackage, String xmlOutPut, boolean useCache) {
        super(entityPackage, mapperPackage, xmlOutPut, useCache, 0, null, -1);
    }

    public CustomMapperConfig(String entityPackage, String mapperPackage, String xmlOutPut, long cacheTime) {
        super(entityPackage, mapperPackage, xmlOutPut, true, cacheTime, null, -1);
    }

    public CustomMapperConfig(String entityPackage, String mapperPackage, String xmlOutPut, String cacheClass) {
        super(entityPackage, mapperPackage, xmlOutPut, true, 0, cacheClass, -1);
    }

    public CustomMapperConfig(String entityPackage, String mapperPackage, String xmlOutPut, boolean useCache, long cacheTime) {
        super(entityPackage, mapperPackage, xmlOutPut, useCache, cacheTime, null, -1);
    }

    public CustomMapperConfig(String entityPackage, String mapperPackage, String xmlOutPut, boolean useCache,
                        long cacheTime, String cacheClass, int maxLimit) {
        super(entityPackage, mapperPackage, xmlOutPut, useCache, cacheTime, cacheClass, maxLimit);
    }

    public boolean isCreateDelete() {
        return createDelete;
    }

    public void setCreateDelete(boolean createDelete) {
        this.createDelete = createDelete;
    }
}
