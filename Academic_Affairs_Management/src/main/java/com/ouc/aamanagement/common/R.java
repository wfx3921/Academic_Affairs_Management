package com.ouc.aamanagement.common;

import lombok.Data;
import java.util.HashMap;
import java.util.Map;


/**
 * 返回通用结果类，服务端响应的数据都会封装成此数据
 * @param <T>
 */
@Data
public class R<T> {


    private Integer code; //编码：1成功，0和其它数字为失败

    private String msg; //错误信息

    private T data; //数据

    private Map map = new HashMap(); //动态数据
    public static <T> R<T> success() {
        R<T> r = new R<T>();
        r.code = 1;
        return r;
    }
    public static <T> R<T> success(T object) {
        R<T> r = new R<T>();
        r.data = object;
        r.code = 1;
        return r;  //data和code被返回，data可能代表实体类数据
    }

    public static <T> R<T> error(String msg) {
        R r = new R();
        r.msg = msg;
        r.code = 0;
        return r;  //msg代表失败的返回信息，失败信息可以是“登录失败，请重新登录”等字符串信息
    }

    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

}
