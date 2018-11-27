package love.wangqi.config;


import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOutboundHandler;
import love.wangqi.codec.HttpRequestBuilder;
import love.wangqi.core.ResponseHandler;
import love.wangqi.exception.handler.ExceptionHandler;
import love.wangqi.filter.FilterRegistry;
import love.wangqi.filter.GatewayFilter;
import love.wangqi.route.RouteMapper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/6/4 下午6:49
 */
public class GatewayConfig {

    public final static GatewayConfig INSTANCE = new GatewayConfig();

    private final ConcurrentHashMap<String, List<GatewayFilter>> hashFiltersByType = new ConcurrentHashMap<String, List<GatewayFilter>>();

    public static GatewayConfig getInstance() {
        return INSTANCE;
    }

    public List<GatewayFilter> getFiltersByType(String filterType) {
        List<GatewayFilter> list = hashFiltersByType.get(filterType);
        if (list != null) {
            return list;
        }

        list = new ArrayList<>();

        Collection<GatewayFilter> filters = FilterRegistry.instance().getAllFilters();
        for (Iterator<GatewayFilter> iterator = filters.iterator(); iterator.hasNext(); ) {
            GatewayFilter filter = iterator.next();
            if (filter.filterType().equals(filterType)) {
                list.add(filter);
            }
        }
        Collections.sort(list);
        hashFiltersByType.putIfAbsent(filterType, list);
        return list;
    }

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
     * channel写完成监听器
     */
    private ChannelFutureListener channelWriteFinishListener;
    /**
     * 异常处理器
     */
    private ExceptionHandler exceptionHandler;
    /**
     * 响应处理器
     */
    private ResponseHandler responseHandler;


    private GatewayConfig() {
        channelInboundHandlerList = new ArrayList<>();
        channelOutboundHandlerList = new ArrayList<>();
        httpResponseHandlerList = new ArrayList<>();
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

    public ChannelFutureListener getChannelWriteFinishListener() {
        return channelWriteFinishListener;
    }

    public void setChannelWriteFinishListener(ChannelFutureListener channelWriteFinishListener) {
        this.channelWriteFinishListener = channelWriteFinishListener;
    }

    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public ResponseHandler getResponseHandler() {
        return responseHandler;
    }

    public void setResponseHandler(ResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
    }
}
