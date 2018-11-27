package love.wangqi.exception.handler;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.*;
import love.wangqi.config.GatewayConfig;
import love.wangqi.exception.GatewayException;

import java.net.ConnectException;
import java.util.concurrent.RejectedExecutionException;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/6/5 下午6:16
 */
public class DefaultExceptionHandler extends AbstractExceptionHandler {
    private GatewayConfig config = GatewayConfig.getInstance();

    @Override
    public ExceptionResponse getExceptionResponse(Exception exception) {
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        if (exception instanceof GatewayException) {
            GatewayException gatewayException = (GatewayException) exception;
            exceptionResponse.setStatus(gatewayException.getStatus());
            exceptionResponse.setContentType("text/plain");
            exceptionResponse.setContent(gatewayException.getMessage());
        } else if (exception instanceof ConnectException) {
            exceptionResponse.setStatus(HttpResponseStatus.NOT_FOUND);
            exceptionResponse.setContentType("text/plain");
            exceptionResponse.setContent("connect server refused");
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
    public void send(Channel channel, ExceptionResponse exceptionResponse) {
        String content = exceptionResponse.getContent();
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, exceptionResponse.getStatus());
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        if (content != null) {
            response.headers().set("X-Ca-Error-Message", content);
        }
        config.getResponseHandler().send(channel, response);
    }
}
