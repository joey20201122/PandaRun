package com.joey.yx.filter;

import com.alibaba.fastjson.JSON;
import com.joey.yx.common.BaseContext;
import com.joey.yx.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录的完善，检查用户是否已经登陆
 */

@WebFilter(filterName = "loginCheckFilter" ,urlPatterns = {"/*"})
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器,支持通配符写法
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //1.获取请求uri
        String requestURI = request.getRequestURI();
        //2.判断这次请求是否需要处理
        //定义无需处理的uri数组
        String[] urls = new String[] {
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login",
        };
        //3.若不需要，则直接放行
        if (check(urls,requestURI)){
            filterChain.doFilter(request,response);
            return;
        }
        //4-1,判断后台登录状态.若需要，则判断是否已经登录
        if(null != request.getSession().getAttribute("employee")){
            //将id存入线程
            Long employeeId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(employeeId);
            //6.若已登录，放行
            filterChain.doFilter(request,response);
            return;
        }
        //4-2.判断用户登录状态，若需要，则判断是否已经登录
        if(null != request.getSession().getAttribute("user")){
            //将id存入线程
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            //6.若已登录，放行
            filterChain.doFilter(request,response);
            return;
        }
        //5.若未登录，通过输出流的方式向客户端响应数据
        response.getWriter().write(JSON.toJSONString(R.error("获取失败!尚未登录!")));
    }

    /**
     * 判断是否路径匹配
     * @param urls
     * @param requestUrl
     * @return
     */
    public boolean check(String[] urls, String requestUrl){
        for (String url : urls) {
            if (PATH_MATCHER.match(url,requestUrl)){
                return true;
            }
        }
        return false;
    }
}
