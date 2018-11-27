package love.wangqi.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import love.wangqi.context.ContextUtil;
import love.wangqi.exception.GatewayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/5/26 21:58
 */
public class FrontHandler extends ChannelInboundHandlerAdapter {
    private final Logger logger = LoggerFactory.getLogger(FrontHandler.class);


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        GatewayRunner runner = null;
        try {
            if (!(msg instanceof FullHttpRequest)) {
                throw new Exception("未知请求");
            }

            FullHttpRequest httpRequest = (FullHttpRequest) msg;
            logger.debug("serverChannelId: {}", ctx.channel().id());
            ContextUtil.setRequest(ctx.channel(), httpRequest);

            Boolean keepAlive = HttpUtil.isKeepAlive(httpRequest);
            ContextUtil.setKeepAlive(ctx.channel(), keepAlive);

            runner = GatewayRunner.getInstance();
            runner.forwardAction(ctx.channel());
        } catch (Throwable e) {
            e.printStackTrace();
            Exception exception = new GatewayException(HttpResponseStatus.INTERNAL_SERVER_ERROR, "UNHANDLED_EXCEPTION_" + e.getClass().getName());
            ContextUtil.setException(ctx.channel(), exception);
            runner.error(ctx.channel());
        }
    }
}
