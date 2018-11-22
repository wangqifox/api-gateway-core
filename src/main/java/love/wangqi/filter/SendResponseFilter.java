package love.wangqi.filter;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import love.wangqi.handler.GatewayRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import love.wangqi.config.GatewayConfig;
import love.wangqi.context.HttpRequestContext;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/9/5 下午2:16
 */
public class SendResponseFilter extends GatewayFilter {
    private static final Logger logger = LoggerFactory.getLogger(SendResponseFilter.class);

    private HttpRequestContext httpRequestContext = HttpRequestContext.getInstance();

    private GatewayConfig config = GatewayConfig.getInstance();

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
            FullHttpResponse response = httpRequestContext.getResponse(httpRequest);
//            logger.info("*** content {}", response.content().toString(Charset.defaultCharset()));
            config.getResponseHandler().send(channel, response);
        } else {
            GatewayRunner.getInstance().errorAction((FullHttpRequest) httpRequestContext.getHttpRequest(channel));
        }
    }
}
