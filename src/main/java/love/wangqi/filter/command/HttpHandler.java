package love.wangqi.filter.command;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.timeout.ReadTimeoutException;
import love.wangqi.context.Attributes;
import love.wangqi.context.ContextUtil;
import love.wangqi.exception.GatewayTimeoutException;
import love.wangqi.handler.GatewayRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018-11-28 08:37
 */
public class HttpHandler extends SimpleChannelInboundHandler<FullHttpResponse> {
    private Logger logger = LoggerFactory.getLogger(HttpHandler.class);

    public HttpHandler() {
        super(false);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpResponse response) throws Exception {
        Channel clientChannel = ctx.channel();
        Channel serverChannel = clientChannel.attr(Attributes.SERVER_CHANNEL).get();

        ContextUtil.setResponse(serverChannel, response);
        GatewayRunner.getInstance().postRoutAction(serverChannel);

        clientChannel.attr(Attributes.CLIENT_POOL).get().release(clientChannel);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive");
        super.channelActive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel serverChannel = ctx.channel().attr(Attributes.SERVER_CHANNEL).get();
        if (cause instanceof ReadTimeoutException) {
            logger.error("read time out");
            Exception exception = new GatewayTimeoutException();
            ContextUtil.setException(serverChannel, exception);
        } else {
            logger.error(cause.getMessage(), cause);
            ContextUtil.setException(serverChannel, new RuntimeException(cause));
        }
        GatewayRunner.getInstance().errorAction(serverChannel);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelInactive");
        super.channelInactive(ctx);
    }
}
