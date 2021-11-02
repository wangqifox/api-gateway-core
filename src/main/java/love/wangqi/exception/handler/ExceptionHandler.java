package love.wangqi.exception.handler;

import io.netty.channel.Channel;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/6/5 下午5:29
 */
public interface ExceptionHandler {
    /**
     * 获取异常返回
     *
     * @param exception
     * @return
     */
    ExceptionResponse getExceptionResponse(Exception exception);

    /**
     * 发送异常
     *
     * @param channel
     * @param exceptionResponse
     */
    void send(Channel channel, ExceptionResponse exceptionResponse);

    /**
     * 处理异常
     *
     * @param channel
     * @param exception
     */
    void handle(Channel channel, Exception exception);
}
