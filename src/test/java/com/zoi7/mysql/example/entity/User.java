package com.zoi7.mysql.example.entity;

import com.zoi7.mysql.comment.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 测试用 > 用户表实体
 * Created by yjy on 2017/9/22.
 */
@Entity(tableName = "test_user", check = true, indices = {
        @UniteIndex(columns = {"NICK_NAME", "SEX"})
})
public class User {

    @Id
    @Field(field = "id", type = FieldType.BIGINT)
    private Long id;
    @Field(field = "user_name", type = FieldType.VARCHAR, length = 50)
    private String userName;
    @Field(field = "password", type = FieldType.VARCHAR, length = 30)
    private String password;
    @Field(nullable = false, defaultCharValue = "游客")
    private String nickName;
    @Field(nullable = false, defaultValue = 1)
    private Integer sex;
    @Field(nullable = false, defaultCharValue = "8454541456231")
    private Long money;
    @Field(nullable = false, defaultCharValue = "0.98")
    private Double health;
    @Field(nullable = false)
    private BigDecimal gold;
    @Field(nullable = false)
    private Integer sex2;
    @Field(nullable = false)
    private BigDecimal gold2;
    @Field(nullable = false, defaultValue = 3)
    private Integer sex3;
    @Field(nullable = false, defaultCharValue = "55")
    private BigDecimal gold3;
    @Field(nullable = false, defaultCharValue = "20181218")
    private Date updateTime;

}
