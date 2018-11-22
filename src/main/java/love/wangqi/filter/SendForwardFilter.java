package love.wangqi.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import love.wangqi.codec.HttpRequestBuilder;
import love.wangqi.codec.RequestHolder;
import love.wangqi.config.GatewayConfig;
import love.wangqi.context.HttpRequestContext;
import love.wangqi.filter.command.ForwardCommand;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/9/3 下午7:53
 */
public class SendForwardFilter extends GatewayFilter {
    private GatewayConfig config = GatewayConfig.getInstance();
    private HttpRequestContext httpRequestContext = HttpRequestContext.getInstance();

    @Override
    public String filterType() {
        return "route";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public void filter(FullHttpRequest httpRequest) throws Exception {
        HttpRequestBuilder httpRequestBuilder = config.getHttpRequestBuilder()
                .setRouteMapper(config.getRouteMapper());

        RequestHolder requestHolder = httpRequestBuilder.build(httpRequest);

        ChannelHandlerContext ctx = httpRequestContext.getChannelHandlerContext(httpRequest);
        ForwardCommand forwardCommand = new ForwardCommand(ctx, requestHolder);
        forwardCommand.execute();
    }
}
