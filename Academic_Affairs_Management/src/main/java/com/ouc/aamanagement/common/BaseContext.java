package com.ouc.aamanagement.common;

/**
 * 基于ThreadLocal封装的工具类，用于保存和获取当前用户的id，用于公共字段填充
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
