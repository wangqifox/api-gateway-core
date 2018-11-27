package love.wangqi.filter.command;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
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
 * @date: Created in 2018-11-26 20:59
 */
public class HttpHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(HttpHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive");
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpResponse response = (FullHttpResponse) msg;
        logger.debug("handler hashCode: {}", this.hashCode());
        logger.debug("clientChannelId: {}", ctx.channel().id());
        Channel serverChannel = ctx.channel().attr(Attributes.SERVER_CHANNEL).get();
        logger.debug("serverChannelId: {}", serverChannel.id());

        ContextUtil.setResponse(serverChannel, response);
//        ctx.channel().close();
        GatewayRunner.getInstance().postRoutAction(serverChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel serverChannel = ctx.channel().attr(Attributes.SERVER_CHANNEL).get();
        logger.debug("serverChannelId: {}", serverChannel.id());
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
