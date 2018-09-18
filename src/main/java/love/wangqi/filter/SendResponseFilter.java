package love.wangqi.filter;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.FullHttpRequest;
import love.wangqi.context.HttpRequestContext;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/9/5 下午2:16
 */
public class SendResponseFilter extends GatewayFilter {
    private HttpRequestContext httpRequestContext = HttpRequestContext.getInstance();

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public void filter(FullHttpRequest httpRequest) throws Exception {
        Channel channel = httpRequestContext.getChannel(httpRequest);
        Exception e = httpRequestContext.getException(httpRequest);
        if (channel != null && e == null) {
            Object response = httpRequestContext.getResponse(httpRequest);
            channel.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
