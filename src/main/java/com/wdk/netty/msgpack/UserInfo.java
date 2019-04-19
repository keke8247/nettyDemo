package com.wdk.netty.msgpack;

import org.msgpack.annotation.Message;

import java.io.Serializable;

/**
 * @Description
 * @Author wangdk, wangdk@erongdu.com
 * @CreatTime 2019/4/19 15:05
 * @Since version 1.0.0
 */
@Message
public class UserInfo{

    private int age;
    private String name;

    /**
     * Gets the value of age.
     *
     * @return the value of age.
     */
    public int getAge() {
        return age;
    }

    /**
     * Sets the age.
     * <p>
     * <p>You can use getAge() to get the value of age</p>
     *
     * @param age age
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Gets the value of name.
     *
     * @return the value of name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     * <p>
     * <p>You can use getName() to get the value of name</p>
     *
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }
}
