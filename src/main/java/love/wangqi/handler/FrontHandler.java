package love.wangqi.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import love.wangqi.context.RequestConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import love.wangqi.context.HttpRequestContext;
import love.wangqi.exception.GatewayException;


/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/5/26 21:58
 */
public class FrontHandler extends ChannelInboundHandlerAdapter {
    private final Logger logger = LoggerFactory.getLogger(FrontHandler.class);

    private HttpRequestContext httpRequestContext = HttpRequestContext.getInstance();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        FullHttpRequest httpRequest = null;
        GatewayRunner runner = null;
        try {
            if (!(msg instanceof FullHttpRequest)) {
                throw new Exception("未知请求");
            }

            httpRequest = (FullHttpRequest) msg;
            httpRequestContext.setChannelHandlerContext(httpRequest, ctx);

            Boolean keepAlive = HttpUtil.isKeepAlive(httpRequest);
            httpRequestContext.set(httpRequest, RequestConstant.KEEPALIVE, keepAlive);

            runner = GatewayRunner.getInstance();
            runner.forwardAction(httpRequest);
        } catch (Throwable e) {
            e.printStackTrace();
            Exception exception = new GatewayException(HttpResponseStatus.INTERNAL_SERVER_ERROR, "UNHANDLED_EXCEPTION_" + e.getClass().getName());
            httpRequestContext.setException(httpRequest, exception);
            runner.error(httpRequest);
        }
    }
}
