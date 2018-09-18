package love.wangqi.context;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/9/3 下午7:31
 */
public class HttpRequestContext {
    /**
     * HttpRequest相关联的数据
     */
    private static final ConcurrentHashMap<Channel, ConcurrentHashMap<String, Object>> channelContext = new ConcurrentHashMap<>();
    /**
     * Channel与HttpRequest的对应关系
     */
    private static final ConcurrentHashMap<HttpRequest, Channel> requestChannel = new ConcurrentHashMap<>();

    private HttpRequestContext() {}

    private static final HttpRequestContext INSTANCE = new HttpRequestContext();

    public static HttpRequestContext getInstance() {
        return INSTANCE;
    }

    public void setRequestChannel(HttpRequest httpRequest, Channel channel) {
        requestChannel.putIfAbsent(httpRequest, channel);
        set(channel, RequestConstant.HTTPREQUEST, httpRequest);
    }

    public ConcurrentHashMap<String, Object> getContext(Channel channel) {
        return channelContext.get(channel);
    }

    public <T> T get(Channel channel, String key) {
        ConcurrentHashMap<String, Object> context = getContext(channel);
        return context == null ? null : (T) context.get(key);
    }

    public void set(Channel channel, String key, Object value) {
        ConcurrentHashMap<String, Object> context = getContext(channel);
        if (context == null) {
            context = new ConcurrentHashMap<>();
            channelContext.putIfAbsent(channel, context);
        }
        context.putIfAbsent(key, value);
    }

    public <T> T get(HttpRequest httpRequest, String key) {
        Channel channel = requestChannel.get(httpRequest);
        return channel == null ? null : get(channel, key);
    }

    public void set(HttpRequest httpRequest, String key, Object value) {
        Channel channel = requestChannel.get(httpRequest);
        set(channel, key, value);
    }

    public void remove(HttpRequest httpRequest) {
        Channel channel = requestChannel.get(httpRequest);
        ConcurrentHashMap<String, Object> map = channelContext.remove(channel);
        requestChannel.remove(httpRequest);
    }

    public void remove(Channel channel) {
        ConcurrentHashMap<String, Object> map = channelContext.remove(channel);
        requestChannel.remove(map.get(RequestConstant.HTTPREQUEST));
    }

    public HttpRequest getHttpRequest(Channel channel) {
        return get(channel, RequestConstant.HTTPREQUEST);
    }

    public Channel getChannel(HttpRequest httpRequest) {
        return requestChannel.get(httpRequest);
    }

    public ChannelHandlerContext getChannelHandlerContext(HttpRequest httpRequest) {
        return (ChannelHandlerContext) get(httpRequest, RequestConstant.CHANNELHANDLERCONTEXT);
    }

    public void setChannelHandlerContext(HttpRequest httpRequest, ChannelHandlerContext ctx) {
        setRequestChannel(httpRequest, ctx.channel());
        set(httpRequest, RequestConstant.CHANNELHANDLERCONTEXT, ctx);
    }

    public Exception getException(HttpRequest httpRequest) {
        return (Exception) get(httpRequest, RequestConstant.EXCEPTION);
    }

    public void setException(HttpRequest httpRequest, Exception exception) {
        set(httpRequest, RequestConstant.EXCEPTION, exception);
    }

    public void setException(Channel channel, Exception exception) {
        set(channel, RequestConstant.EXCEPTION, exception);
    }

    public void setResponse(Channel channel, Object response) {
        set(channel, RequestConstant.RESPONSE, response);
    }

    public Object getResponse(Channel channel) {
        return get(channel, RequestConstant.RESPONSE);
    }

    public Object getResponse(HttpRequest httpRequest) {
        return get(httpRequest, RequestConstant.RESPONSE);
    }
}
