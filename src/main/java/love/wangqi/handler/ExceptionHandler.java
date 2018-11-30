package love.wangqi.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpResponseStatus;
import love.wangqi.context.ContextUtil;
import love.wangqi.exception.GatewayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018-11-30 09:14
 */
public class ExceptionHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause.getMessage(), cause);
        GatewayRunner runner = GatewayRunner.getInstance();
        Exception exception = new GatewayException(HttpResponseStatus.INTERNAL_SERVER_ERROR, "UNHANDLED_EXCEPTION_" + cause.getClass().getName());
        ContextUtil.setException(ctx.channel(), exception);
        runner.errorAction(ctx.channel());
    }
}
