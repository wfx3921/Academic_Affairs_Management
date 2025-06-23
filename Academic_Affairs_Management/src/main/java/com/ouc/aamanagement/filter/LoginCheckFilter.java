package com.ouc.aamanagement.filter;

import com.alibaba.fastjson.JSON;
import com.ouc.aamanagement.common.BaseContext;
import com.ouc.aamanagement.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 过滤器，防止客户端直接通过URL不通过登录页面进入首页
 */
//@WebFilter 是 Java Servlet 规范中的一项标准注解，用于定义过滤器。可被启动类上的注解 @ServletComponentScan 扫描到。
//filterName 代表过滤器的名字，值可以随意设置。urlPatterns 代表拦截的要拦截什么样的url请求，/* 代表要拦截所有的对Controller层的url请求
//@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {    //Filter接口是javax.servlet包下的

    /**
     * 做URL路径匹配用
     */
    public static final AntPathMatcher Path_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;

        //1.获取本次请求的URI
        String requestURI = request.getRequestURI();
        log.info("拦截到请求：{}",requestURI);

        //2.判断本次请求是否需要处理，这些都是请求路径，请求路径不一定是相应的文件地址
        String[] urls = new String[]{
                //前端的分页查询请求，打开前端首页面中的“用户管理”，都会向后台发送一个分页查询请求
                "/user/page",
                "/patientdata/page",
                "/backend/**",
                //这个common请求路径对应的不是common包下的class文件，而是浏览器请求路径，将这个路径设置为不拦截是为了方便测试文件上传下载功能
                "/common/**"
        };
        //对所有静态资源设置成不拦截，但是对Controller层的url访问要被拦截，所以可以打开静态页面，但无法通过静态页面的url访问Controller层
        boolean check = check(urls,requestURI);

        //3.如果不需要处理，则直接放行
        if (check){
            //将此URL直接放行，这行代码的主要作用是将请求继续在过滤器链中传递，以便后续的过滤器和 Servlet 可以继续处理请求
            filterChain.doFilter(request,response);
            log.info("不需要处理的被放行的URI：{}",requestURI);
            return;
        }

        //4.判断登录状态，如果已经登录则直接放行(判断Session中是否为空，如果不为空则说明已经有登录的账户了)
        /**
         * 如果不加response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));代码，当登录后会将所有的url请求都放行，不放行的话虽然可以进入首页面，但是会拦截"/employee/page"请求，
         * 如果加了 response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN"))); 代码之后，则无法通过不登录的方式进入首页面了
         */
        if(request.getSession().getAttribute("user") != null){
            //获取当前线程的id
            long id = Thread.currentThread().getId();
            log.info("LoginCheckFilter类中的当前线程的id：{}",id);

            //将当前线程id的值设置成当前登录用户的id，用于公共字段值的填充
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            //将此URL直接放行，这行代码的主要作用是将请求继续在过滤器链中传递，以便后续的过滤器和 Servlet 可以继续处理请求
            filterChain.doFilter(request,response);
            log.info("用户已经登录，id：{}",request.getSession().getAttribute("user"));
            return;
        }

        //5.如果未登录则返回未登录的结果，通过输出流的方式向前端响应对象
        /**
         * 加了此行代码之后，不登录的情况下访问其他页面都会被返回到登录页面
         */
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));  //error函数的实参必须是“NOTLOGIN”，因为backend/js包下的request.js中响应拦截器函数只能接受此参数
    }

    /**
     * 判断当前的URI是否应该被放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = Path_MATCHER.match(url,requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }
}
