package com.ouc.aamanagement.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自定义元数据对象处理器
 * 此类用来处理公共字段填充的功能
 * 当前类中无法获得HttpSession对象，所以获取用户ID可以使用ThreadLocal类
 */
@Slf4j
@Component
public class MyMetaObjecthandler implements MetaObjectHandler {
    /**
     * 插入操作，自动填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]....");
        log.info(metaObject.toString());
        //参数值为实体类中的属性名和为其赋的属性值
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        //metaObject.setValue("createUser",new Long(1));
        //metaObject.setValue("updateUser",new Long(1));

        //使用ThreadLocal类处理公共字段填充问题
        metaObject.setValue("createUser",BaseContext.getCurrentId());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());

    }

    /**
     * 更新操作，自动填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充[update]....");
        log.info(metaObject.toString());
        //参数值为实体类中的属性名和为其赋的属性值

        long id = Thread.currentThread().getId();
        log.info("MymetaObjecthandler中的update()方法的当前线程的id：{}",id);

        metaObject.setValue("updateTime", LocalDateTime.now());
        //metaObject.setValue("updateUser",new Long(1));

        metaObject.setValue("updateUser",BaseContext.getCurrentId());

    }
}
