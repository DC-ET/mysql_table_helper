package com.yjy.mysql.example.entity;

import com.yjy.mysql.comment.Entity;
import com.yjy.mysql.comment.Field;
import com.yjy.mysql.comment.FieldType;
import com.yjy.mysql.comment.Id;

/**
 * 测试用 > 用户表实体
 * Created by yjy on 2017/9/22.
 */
@Entity(tableName = "test_user", check = true)
public class User {

    @Id
    @Field(field = "id", type = FieldType.BIGINT)
    private Long id;

    @Field(field = "user_name", type = FieldType.VARCHAR, length = 50)
    private String userName;

    @Field(field = "password", type = FieldType.VARCHAR, length = 30)
    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
