package love.wangqi.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpUtil;
import love.wangqi.config.GatewayConfig;
import love.wangqi.context.ContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/9/28 下午6:14
 */
public class ResponseHandler {
    private Logger logger = LoggerFactory.getLogger(ResponseHandler.class);

    private GatewayConfig config = GatewayConfig.getInstance();

    public void send(Channel channel, FullHttpResponse response) {
        logger.debug("======= serverChannelId: {}", channel.id());
        logger.debug("readableBytes {}", response.content().readableBytes());
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        Boolean keepAlive = ContextUtil.getKeepAlive(channel);
        HttpUtil.setKeepAlive(response, keepAlive == null ? false : keepAlive);
        ChannelFuture future = channel.writeAndFlush(response);
        if (config.getChannelWriteFinishListener() != null) {
            future.addListener(config.getChannelWriteFinishListener());
        }
    }
}
