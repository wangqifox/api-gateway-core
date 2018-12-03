package love.wangqi.context;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import love.wangqi.codec.RequestHolder;


/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018-11-27 15:02
 */
public class ContextUtil {
    public static void setRequest(Channel channel, FullHttpRequest request) {
        channel.attr(Attributes.REQUEST).set(request);
    }

    public static FullHttpRequest getRequest(Channel channel) {
        return channel.attr(Attributes.REQUEST).get();
    }

    public static void setResponse(Channel channel, FullHttpResponse response) {
        channel.attr(Attributes.RESPONSE).set(response);
    }

    public static FullHttpResponse getResponse(Channel channel) {
        return channel.attr(Attributes.RESPONSE).get();
    }

    public static void setKeepAlive(Channel channel, Boolean keepAlive) {
        channel.attr(Attributes.KEEPALIVE).set(keepAlive);
    }

    public static Boolean getKeepAlive(Channel channel) {
        return channel.attr(Attributes.KEEPALIVE).get();
    }

    public static void setRequestHolder(Channel channel, RequestHolder requestHolder) {
        channel.attr(Attributes.REQUEST_HOLDER).set(requestHolder);
    }

    public static RequestHolder getRequestHolder(Channel channel) {
        return channel.attr(Attributes.REQUEST_HOLDER).get();
    }

    public static void setException(Channel channel, Exception exception) {
        channel.attr(Attributes.EXCEPTION).set(exception);
    }

    public static Exception getException(Channel channel) {
        return channel.attr(Attributes.EXCEPTION).get();
    }

    public static void clear(Channel channel) {
        channel.attr(Attributes.REQUEST).set(null);
        channel.attr(Attributes.RESPONSE).set(null);
        channel.attr(Attributes.KEEPALIVE).set(null);
        channel.attr(Attributes.EXCEPTION).set(null);
    }
}
