package love.wangqi.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.timeout.ReadTimeoutException;
import love.wangqi.context.ContextUtil;
import love.wangqi.exception.GatewayTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/5/28 下午4:57
 */
public class BackendHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(BackendHandler.class);

    private Channel serverChannel;

    BackendHandler(Channel serverChannel) {
        this.serverChannel = serverChannel;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ContextUtil.setResponse(serverChannel, (FullHttpResponse) msg);
        ctx.channel().close();
        GatewayRunner.getInstance().postRoutAction(serverChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
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
}
