package com.zoi7.mysql.comment;

/**
 * 支持的字段类型
 * @author yjy
 * 2017年2月24日上午9:36:45
 */
public enum FieldType {

    /**
     * 自动匹配类型
     */
	AUTO,
    /**
     * 可以取值为 1、0 或 NULL 的 integer 数据类型
     */
    BIT,
    /**
     * 长整数
     */
    BIGINT,
    /**
     * 定长字符串
     */
    CHAR,
    /**
     * 字符串
     */
    VARCHAR,
    /**
     * 字符串
     */
    LONGVARCHAR,
    /**
     * 二进制大对象，是一个可以存储二进制文件的容器
     */
    BLOB,
    /**
     * CLOB使用CHAR来保存数据。 　如：保存XML文档。
     */
    CLOB,
    /**
     * 整数
     */
    INTEGER,
    /**
     * 超小整数
     */
    TINYINT,
    /**
     * 小整数
     */
    SMALLINT,
    /**
     * 单精度浮点数
     */
    FLOAT,
    /**
     * 双精度浮点数
     */
    DOUBLE,
    /**
     * 小数
     */
    DECIMAL,
    /**
     * 文本
     */
    TINYTEXT,
    /**
     * 文本
     */
    TEXT,
    /**
     * 中等文本
     */
    MEDIUMTEXT,
    /**
     * 长文本
     */
    LONGTEXT,
    /**
     * 日期
     */
    DATE,
    /**
     * 日期时间
     */
    DATETIME,
    /**
     * 时间
     */
    TIME,
    /**
     * 时间戳
     */
    TIMESTAMP,

}
