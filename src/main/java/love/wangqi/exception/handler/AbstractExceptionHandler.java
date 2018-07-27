package love.wangqi.exception.handler;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/6/5 下午6:08
 */
public abstract class AbstractExceptionHandler implements ExceptionHandler {

    @Override
    public final void handle(ChannelHandlerContext ctx, Exception exception) {
        ExceptionResponse exceptionResponse = getExceptionResponse(exception);
        send(ctx, exceptionResponse);
    }
}
