package love.wangqi.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import love.wangqi.codec.DefaultHttpRequestBuilder;
import love.wangqi.codec.HttpRequestBuilder;
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

            HttpRequestBuilder httpRequestBuilder = GatewayServer.config.getHttpRequestBuilder()
                    .setRouteMapper(GatewayServer.config.getRouteMapper())
                    .setOriginRequest(httpRequest);

            DefaultHttpRequestBuilder.RequestHolder requestHolder = httpRequestBuilder.build();
            ForwardCommand forwardCommand = new ForwardCommand(ctx, requestHolder);
            forwardCommand.queue();
        } catch (Exception e) {
            logger.error(e.toString());
            GatewayServer.config.getExceptionHandler().handle(ctx, e);
        } finally {
            httpRequest.release();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("连接的客户端地址：{}", ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }
}
