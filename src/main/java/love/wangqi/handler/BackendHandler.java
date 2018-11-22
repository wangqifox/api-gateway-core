package love.wangqi.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.timeout.ReadTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import love.wangqi.context.HttpRequestContext;
import love.wangqi.exception.GatewayTimeoutException;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/5/28 下午4:57
 */
public class BackendHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(BackendHandler.class);

    private ChannelHandlerContext ctx;
    private HttpRequestContext httpRequestContext = HttpRequestContext.getInstance();

    BackendHandler(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        httpRequestContext.setResponse(this.ctx.channel(), (FullHttpResponse) msg);
        ctx.channel().close();
        GatewayRunner.getInstance().postRoutAction((FullHttpRequest) httpRequestContext.getHttpRequest(this.ctx.channel()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof ReadTimeoutException) {
            logger.error("read time out");
            Exception exception = new GatewayTimeoutException();
            httpRequestContext.setException(this.ctx.channel(), exception);
        } else {
            logger.error(cause.getMessage(), cause);
            httpRequestContext.setException(this.ctx.channel(), new RuntimeException(cause));
        }
        GatewayRunner.getInstance().errorAction((FullHttpRequest) httpRequestContext.getHttpRequest(this.ctx.channel()));
    }
}
