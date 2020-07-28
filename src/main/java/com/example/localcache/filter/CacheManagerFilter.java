package com.example.localcache.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Component
public class CacheManagerFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("doFilter");
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        response.setStatus(HttpStatus.OK.value());
        PrintWriter writer = response.getWriter();
        writer.println("hello");
        writer.flush();
        writer.close();
    }
}
