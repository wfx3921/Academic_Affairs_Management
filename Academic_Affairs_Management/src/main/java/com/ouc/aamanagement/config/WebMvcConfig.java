package com.ouc.aamanagement.config;

import com.ouc.aamanagement.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;


/**
 * 在 springboot 中静态资源需要放到resources目录下的 static 或 templates 文件夹中，但是在瑞吉外卖项目中，我们把静态资源放在了
 * backend（后台管理界面 ）和 front（用户界面）文件夹下，所以需要此类进行配置以便让 springboot 框架访问到静态资源。
 */
/*
 * 配置类必须加 @Configuration 注解
 */
@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    /**
     * 设置静态资源映射
     * 这段代码的作用是告诉Springboot框架，在处理以"/backend/"开头的URL（例如：http://localhost:8080/backend/index.html）请求时，
     * 应该从类路径中的"/backend/"目录加载静态资源（如CSS、JavaScript、图像等），以便这些资源可以被浏览器请求并加载。
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {

        log.info("开始进行静态资源映射....");
        /**
         * addResourceHandler()指定了一个资源处理器的映射路径,任何以"/backend/"开头的URL请求将由这个资源处理器处理。
         * "/backend/**"表示可以访问到backend文件下的所有文件，**是通配符
         *
         * addResourceLocations()定义了资源的位置,它告诉Spring框架将资源从类路径（classpath）中的"/backend/"目录加载出来。
         * "classpath:/backend/"代表访问路径，classpath指的是resources文件
         */
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/uploads/diplomas/**")
                .addResourceLocations("file:C:/uploads/diplomas/"); // 注意斜杠方向
    }

    /**
     * 扩展mvc框架的消息转换器
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用Jackson将Java对象转换成json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将上面的消息转换器对象追加到mvc框架的转换器集合中，下标为0的意思是让mvc框架使用我们自定义的转换器，要不然mvc框架会使用它自带的转换器。
        converters.add(0,messageConverter);
        super.extendMessageConverters(converters);
    }



}
