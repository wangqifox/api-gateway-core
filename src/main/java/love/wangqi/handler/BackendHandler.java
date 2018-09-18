package love.wangqi.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import love.wangqi.context.HttpRequestContext;

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
        httpRequestContext.setResponse(this.ctx.channel(), msg);
        ctx.channel().close();
    }
}
