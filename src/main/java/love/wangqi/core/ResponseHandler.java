package love.wangqi.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import love.wangqi.config.GatewayConfig;
import love.wangqi.context.HttpRequestContext;
import love.wangqi.context.RequestConstant;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/9/28 下午6:14
 */
public class ResponseHandler {
    private Logger logger = LoggerFactory.getLogger(ResponseHandler.class);

    private GatewayConfig config = GatewayConfig.getInstance();
    private HttpRequestContext httpRequestContext = HttpRequestContext.getInstance();

    public void send(Channel channel, FullHttpResponse response) {
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        Boolean keepAlive = httpRequestContext.get(channel, RequestConstant.KEEPALIVE);
        HttpUtil.setKeepAlive(response, keepAlive == null ? false : keepAlive);
        channel.writeAndFlush(response).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (config.getChannelCloseFutureListener() != null) {
                    config.getChannelCloseFutureListener().operationComplete(channel, future);
                }
            }
        });
    }
}
