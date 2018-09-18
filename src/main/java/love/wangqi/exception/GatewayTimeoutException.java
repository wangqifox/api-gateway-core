package love.wangqi.exception;

import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/7/28 09:37
 */
public class GatewayTimeoutException extends GatewayException {

    public GatewayTimeoutException() {
        super(HttpResponseStatus.REQUEST_TIMEOUT, "timeout");
    }
}
