package love.wangqi.config;


import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOutboundHandler;
import love.wangqi.codec.HttpRequestBuilder;
import love.wangqi.exception.handler.ExceptionHandler;
import love.wangqi.filter.HttpRequestFilter;
import love.wangqi.listener.ChannelCloseFutureListener;
import love.wangqi.route.RouteMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/6/4 下午6:49
 */
public class GatewayConfig {
    /**
     * 读入流量处理器
     */
    private List<ChannelInboundHandler> channelInboundHandlerList;
    /**
     * 写出流量处理器
     */
    private List<ChannelOutboundHandler> channelOutboundHandlerList;
    /**
     * HttpResponse的处理器
     */
    private List<ChannelOutboundHandler> httpResponseHandlerList;
    /**
     * 路由映射器
     */
    private RouteMapper routeMapper;
    /**
     * http请求构建器
     */
    private HttpRequestBuilder httpRequestBuilder;
    /**
     * 服务端口
     */
    private int port;
    /**
     * channel关闭监听器
     */
    private ChannelCloseFutureListener channelCloseFutureListener;
    /**
     * HttpRequest过滤器
     */
    private List<HttpRequestFilter> httpRequestFilterList;
    /**
     * 异常处理器
     */
    private ExceptionHandler exceptionHandler;


    public GatewayConfig() {
        channelInboundHandlerList = new ArrayList<>();
        channelOutboundHandlerList = new ArrayList<>();
        httpResponseHandlerList = new ArrayList<>();
        httpRequestFilterList = new ArrayList<>();
    }

    public void addChannelInboundHandler(ChannelInboundHandler channelInboundHandler) {
        channelInboundHandlerList.add(channelInboundHandler);
    }

    public List<ChannelInboundHandler> getChannelInboundHandlerList() {
        return channelInboundHandlerList;
    }

    public void addChannelOutboundHandler(ChannelOutboundHandler channelOutboundHandler) {
        channelOutboundHandlerList.add(channelOutboundHandler);
    }

    public List<ChannelOutboundHandler> getChannelOutboundHandlerList() {
        return channelOutboundHandlerList;
    }

    public void addHttpResponseHandler(ChannelOutboundHandler channelOutboundHandler) {
        httpResponseHandlerList.add(channelOutboundHandler);
    }

    public List<ChannelOutboundHandler> getHttpResponseHandlerList() {
        return httpResponseHandlerList;
    }

    public RouteMapper getRouteMapper() {
        return routeMapper;
    }

    public void setRouteMapper(RouteMapper routeMapper) {
        this.routeMapper = routeMapper;
    }

    public HttpRequestBuilder getHttpRequestBuilder() {
        return httpRequestBuilder;
    }

    public void setHttpRequestBuilder(HttpRequestBuilder httpRequestBuilder) {
        this.httpRequestBuilder = httpRequestBuilder;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public ChannelCloseFutureListener getChannelCloseFutureListener() {
        return channelCloseFutureListener;
    }

    public void setChannelCloseFutureListener(ChannelCloseFutureListener channelCloseFutureListener) {
        this.channelCloseFutureListener = channelCloseFutureListener;
    }

    public void addHttpRequestFilter(HttpRequestFilter httpRequestFilter) {
        httpRequestFilterList.add(httpRequestFilter);
    }

    public List<HttpRequestFilter> getHttpRequestFilterList() {
        return httpRequestFilterList;
    }

    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }
}
