package com.flyingpig.chat.util;


public class UserContext {
    private static final ThreadLocal<String> tl =new ThreadLocal<>();

    //保存当前登录用户信息到ThreadLocal
    public static void setUser(String userId){
        tl.set(userId);
    }
    //获取当前登录的用户信息
    public static String getUser(){
        return tl.get();
    }

    //移除当前登录用户信息
    public static void removeUser(){
        tl.remove();
    }
}