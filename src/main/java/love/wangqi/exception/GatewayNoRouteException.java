package love.wangqi.exception;

import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/6/5 下午7:26
 */
public class GatewayNoRouteException extends GatewayException {

    public GatewayNoRouteException() {
        super(HttpResponseStatus.NOT_FOUND, "no route found");
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
