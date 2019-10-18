package com.example.demo.filter;


import com.example.demo.util.HttpHelper;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * 防止请求读取后失效，重写包装器
 *
 * @author liliguang
 */

@WebFilter(filterName = "httpServletRequestWrapperFilter", urlPatterns = {"/*"})
@Order(value = 1)
@Component
public class HttpServletRequestWrapperFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        ServletRequest requestWrapper = null;
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            // 遇到post方法才对request进行包装
            String methodType = httpRequest.getMethod();
            // 上传文件时同样不进行包装
            String servletPath = httpRequest.getRequestURI().toString();
            if ("POST".equals(methodType) && !servletPath.contains("/material/upload")) {
                requestWrapper = new BodyReaderHttpServletRequestWrapper(
                        (HttpServletRequest) request);
            }
        }
        if (null == requestWrapper) {
            chain.doFilter(request, response);
        } else {
            chain.doFilter(requestWrapper, response);
        }

    }

    @Override
    public void destroy() {

    }


    /**
     * 重写包装器
     */
    class BodyReaderHttpServletRequestWrapper extends HttpServletRequestWrapper {
        private final byte[] body;

        public BodyReaderHttpServletRequestWrapper(HttpServletRequest request) {
            super(request);
            String bodyString = HttpHelper.getBodyString(request);
            body = bodyString.getBytes(Charset.forName("UTF-8"));
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            final ByteArrayInputStream bais = new ByteArrayInputStream(body);

            return new ServletInputStream() {
                @Override
                public int read() throws IOException {
                    return bais.read();
                }

                @Override
                public boolean isFinished() {
                    return false;
                }

                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setReadListener(ReadListener readListener) {
                }
            };
        }
    }
}
