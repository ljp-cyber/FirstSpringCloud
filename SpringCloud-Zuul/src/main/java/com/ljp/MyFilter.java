package com.ljp;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

@Component
public class MyFilter extends ZuulFilter{
	
	DefaultSingletonBeanRegistry d;
	
	public MyFilter() {
		System.out.println("MyFilter construct");
	}
	
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * 我已经用springSecurity OAuth2实现身份验证、鉴权等，这里应该可以禁用一些filter，可以到yml文件里面里配置
     * 
     * zuul有4种过滤链：
     * 1、pre这种过滤器在请求被路由之前调用。我们可利用这种过滤器实现身份验证、鉴权、限流、参数校验、请求转发，
     * 在集群中选择请求的微服务、记录调试信息等
     * 2、ROUTING这种过滤器将请求路由到微服务。这种过滤器用于构建发送给微服务的请求，
     * 并使用 Apache HttpClient 或 Netfilx Ribbon 请求微服务。
     * 3、POST这种过滤器在路由到微服务以后执行。这种过滤器可用来为响应添加标准的 HTTP Header、
     * 收集统计信息和指标、将响应从微服务发送给客户端等
     * 4、ERROR在其他阶段发生错误时执行该过滤器。 
     * 5、除了默认的过滤器类型，Zuul 还允许我们创建自定义的过滤器类型。
     * 例如，我们可以定制一种 STATIC 类型的过滤器，直接在 Zuul 中生成响应，而不将请求转发到后端的微服务。
     */
    @Override
    public Object run() {
    	System.out.println("MyFilter run");
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        System.out.println(String.format("%s >>> %s", request.getMethod(), request.getRequestURL().toString()));
        String accessToken = request.getParameter("access_token");
        if(accessToken==null)accessToken=request.getHeader("Authorization");
        if(accessToken == null) {
        	System.out.println("token is in the session");
//            ctx.setSendZuulResponse(false);
//            ctx.setResponseStatusCode(401);
//            try {
//                ctx.getResponse().getWriter().write("token is empty");
//            }catch (Exception e){}

            return null;
        }
        System.out.println("ok");
        return null;
    }
}
