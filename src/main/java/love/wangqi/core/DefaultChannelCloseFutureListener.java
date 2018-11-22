package love.wangqi.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.FullHttpRequest;
import love.wangqi.context.HttpRequestContext;
import love.wangqi.context.RequestConstant;
import love.wangqi.listener.ChannelCloseFutureListener;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/11/22 下午4:21
 */
public class DefaultChannelCloseFutureListener implements ChannelCloseFutureListener {
    private HttpRequestContext httpRequestContext = HttpRequestContext.getInstance();

    @Override
    public void operationComplete(Channel channel, ChannelFuture future) {
        Boolean keepAlive = httpRequestContext.get(channel, RequestConstant.KEEPALIVE);

        ((FullHttpRequest)httpRequestContext.getHttpRequest(channel)).release();
        httpRequestContext.remove(channel);

        if (!keepAlive) {
            channel.close();
        }
    }
}
