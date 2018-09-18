package love.wangqi.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import love.wangqi.config.GatewayConfig;
import love.wangqi.context.HttpRequestContext;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/9/3 下午7:56
 */
public class SendErrorFilter extends GatewayFilter {
    private final static Logger logger = LoggerFactory.getLogger(SendErrorFilter.class);

    private GatewayConfig config = GatewayConfig.getInstance();
    private HttpRequestContext httpRequestContext = HttpRequestContext.getInstance();

    @Override
    public String filterType() {
        return "error";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public void filter(FullHttpRequest httpRequest) throws Exception {
        ChannelHandlerContext ctx = httpRequestContext.getChannelHandlerContext(httpRequest);
        Exception e = httpRequestContext.getException(httpRequest);
        if (e != null) {
            logger.error(e.toString());
            config.getExceptionHandler().handle(ctx, e);
        }
    }
}
