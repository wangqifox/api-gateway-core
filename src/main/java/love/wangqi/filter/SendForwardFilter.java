package love.wangqi.filter;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import love.wangqi.codec.HttpRequestBuilder;
import love.wangqi.codec.RequestHolder;
import love.wangqi.config.GatewayConfig;
import love.wangqi.context.ContextUtil;
import love.wangqi.handler.back.BackClientPool;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/9/3 下午7:53
 */
public class SendForwardFilter extends GatewayFilter {
    private GatewayConfig config = GatewayConfig.getInstance();

    @Override
    public String filterType() {
        return "route";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public synchronized void filter(Channel channel) throws Exception {
        HttpRequestBuilder httpRequestBuilder = config.getHttpRequestBuilder()
                .setRouteMapper(config.getRouteMapper());

        FullHttpRequest httpRequest = ContextUtil.getRequest(channel);
        RequestHolder requestHolder = httpRequestBuilder.build(httpRequest);
        httpRequest.release();
        ContextUtil.setRequestHolder(channel, requestHolder);

        BackClientPool.INSTANCE.request(requestHolder, channel);
    }
}
