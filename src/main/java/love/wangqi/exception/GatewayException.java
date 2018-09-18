package love.wangqi.exception;

import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/9/3 下午6:39
 */
public class GatewayException extends RuntimeException {
    private HttpResponseStatus status;
    private String message;

    public GatewayException(HttpResponseStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }

    public HttpResponseStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
