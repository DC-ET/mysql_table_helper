package com.yjy.mysql.example.entity;

import com.yjy.mysql.comment.Entity;
import com.yjy.mysql.comment.Field;
import com.yjy.mysql.comment.FieldType;
import com.yjy.mysql.comment.Id;

import java.util.Date;

/**
 * @author yjy
 * 2018-05-28 17:49
 */
@Entity(tableName = "user1")
public class User1 {

    @Id
    @Field
    private Long id;
    @Field
    private String username;
    @Field(length = 30)
    private String nickName;
    @Field
    private Integer sex;
    @Field
    private int type;
    @Field
    private Date addTime;
    @Field
    private Double money;
    @Field(type = FieldType.VARCHAR, length = 50)
    private Integer intToStr;
    @Field(nullable = false)
    private Integer notNull;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public Integer getIntToStr() {
        return intToStr;
    }

    public void setIntToStr(Integer intToStr) {
        this.intToStr = intToStr;
    }

    public Integer getNotNull() {
        return notNull;
    }

    public void setNotNull(Integer notNull) {
        this.notNull = notNull;
    }
}
