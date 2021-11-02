package love.wangqi.filter;

import io.netty.channel.Channel;
import love.wangqi.codec.RequestHolder;
import love.wangqi.config.GatewayConfig;
import love.wangqi.context.ContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/9/3 下午7:56
 */
public class SendErrorFilter extends GatewayFilter {
    private final static Logger logger = LoggerFactory.getLogger(SendErrorFilter.class);

    private GatewayConfig config = GatewayConfig.getInstance();

    @Override
    public String filterType() {
        return "error";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public void filter(Channel channel) throws Exception {
        Exception e = ContextUtil.getException(channel);
        if (e != null) {
            RequestHolder requestHolder = ContextUtil.getRequestHolder(channel);
            if (requestHolder != null) {
                logger.error("Route Id: " + requestHolder.route.getId() + " Route Url: " + requestHolder.route.getMapUrl() + " " + e.getMessage(), e);
            }
            config.getExceptionHandler().handle(channel, e);
            ContextUtil.clear(channel);
        }
    }
}
