package love.wangqi.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import love.wangqi.codec.DefaultHttpRequestBuilder;
import love.wangqi.codec.HttpRequestBuilder;
import love.wangqi.codec.RequestHolder;
import love.wangqi.filter.HttpRequestFilter;
import love.wangqi.handler.command.ForwardCommand;
import love.wangqi.server.GatewayServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/5/26 21:58
 */
public class FrontHandler extends ChannelInboundHandlerAdapter {
    private final Logger logger = LoggerFactory.getLogger(FrontHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof FullHttpRequest)) {
            GatewayServer.config.getExceptionHandler().handle(ctx, new Exception("未知请求"));
            return;
        }

        FullHttpRequest httpRequest = (FullHttpRequest) msg;
        try {
            for (HttpRequestFilter httpRequestFilter : GatewayServer.config.getHttpRequestFilterList()) {
                httpRequestFilter.filter(GatewayServer.config, ctx, httpRequest);
            }

            HttpRequestBuilder httpRequestBuilder = new DefaultHttpRequestBuilder()
                    .setRouteMapper(GatewayServer.config.getRouteMapper());

            RequestHolder requestHolder = httpRequestBuilder.build(httpRequest);

            ForwardCommand forwardCommand = new ForwardCommand(ctx, requestHolder);
            forwardCommand.queue();
        } catch (Exception e) {
            logger.error(e.toString());
            GatewayServer.config.getExceptionHandler().handle(ctx, e);
        }
        finally {
            httpRequest.release();
        }
    }
}
