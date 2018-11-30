package love.wangqi.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import love.wangqi.context.ContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/5/26 21:58
 */
public class FrontHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final Logger logger = LoggerFactory.getLogger(FrontHandler.class);

    public FrontHandler() {
        super(false);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest httpRequest) {
        Boolean keepAlive = HttpUtil.isKeepAlive(httpRequest);
        ContextUtil.setKeepAlive(ctx.channel(), keepAlive);
        ContextUtil.setRequest(ctx.channel(), httpRequest);

        GatewayRunner runner = GatewayRunner.getInstance();
        runner.forwardAction(ctx.channel());
    }
}
