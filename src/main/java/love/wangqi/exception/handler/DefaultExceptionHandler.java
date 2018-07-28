package love.wangqi.exception.handler;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import love.wangqi.exception.NoRouteException;
import love.wangqi.exception.TimeoutException;

import java.net.ConnectException;
import java.util.concurrent.RejectedExecutionException;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/6/5 下午6:16
 */
public class DefaultExceptionHandler extends AbstractExceptionHandler {
    @Override
    public ExceptionResponse getExceptionResponse(Exception exception) {
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        if (exception instanceof NoRouteException) {
            exceptionResponse.setStatus(HttpResponseStatus.NOT_FOUND);
            exceptionResponse.setContentType("text/plain");
            exceptionResponse.setContent(exception.getMessage());
        } else if (exception instanceof ConnectException) {
            exceptionResponse.setStatus(HttpResponseStatus.NOT_FOUND);
            exceptionResponse.setContentType("text/plain");
            exceptionResponse.setContent("connect server refused");
        } else if (exception instanceof TimeoutException) {
            exceptionResponse.setStatus(HttpResponseStatus.REQUEST_TIMEOUT);
            exceptionResponse.setContentType("text/plain");
            exceptionResponse.setContent("request timeout");
        } else if (exception instanceof RejectedExecutionException) {
            exceptionResponse.setStatus(HttpResponseStatus.TOO_MANY_REQUESTS);
            exceptionResponse.setContentType("text/plain");
            exceptionResponse.setContent("too many requests");
        } else {
            exceptionResponse.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
            exceptionResponse.setContentType("text/plain");
            exceptionResponse.setContent(exception.getMessage());
        }
        return exceptionResponse;
    }

    @Override
    public void send(ChannelHandlerContext ctx, ExceptionResponse exceptionResponse) {
        String content = exceptionResponse.getContent();
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, exceptionResponse.getStatus());
        if (content != null) {
            response.headers().set("X-Ca-Error-Message", content);
        }
        ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        ctx.channel().close();
    }
}
