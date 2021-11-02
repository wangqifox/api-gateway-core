package love.wangqi.filter;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpResponseStatus;
import love.wangqi.config.GatewayConfig;
import love.wangqi.exception.GatewayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/9/3 下午6:32
 */
public class FilterProcessor {
    private final static Logger logger = LoggerFactory.getLogger(FilterProcessor.class);
    private final static FilterProcessor INSTANCE = new FilterProcessor();

    public static FilterProcessor getInstance() {
        return INSTANCE;
    }

    private FilterProcessor() {
    }

    public void preRoute(Channel channel) throws GatewayException {
        try {
            runFilters("pre", channel);
        } catch (GatewayException e) {
            throw e;
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            throw new GatewayException(HttpResponseStatus.INTERNAL_SERVER_ERROR, "UNCAUGHT_EXCEPTION_IN_PRE_FILTER_" + e.getClass().getName());
        }
    }

    public void route(Channel channel) throws GatewayException {
        try {
            runFilters("route", channel);
        } catch (GatewayException e) {
            throw e;
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            throw new GatewayException(HttpResponseStatus.INTERNAL_SERVER_ERROR, "UNCAUGHT_EXCEPTION_IN_PRE_FILTER_" + e.getClass().getName());
        }
    }

    public void postRoute(Channel channel) throws GatewayException {
        try {
            runFilters("post", channel);
        } catch (GatewayException e) {
            throw e;
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            throw new GatewayException(HttpResponseStatus.INTERNAL_SERVER_ERROR, "UNCAUGHT_EXCEPTION_IN_PRE_FILTER_" + e.getClass().getName());
        }
    }

    public void error(Channel channel) {
        try {
            runFilters("error", channel);
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
    }


    public void runFilters(String type, Channel channel) throws Throwable {
        List<GatewayFilter> preFilterList = GatewayConfig.getInstance().getFiltersByType(type);
        if (preFilterList != null) {
            for (GatewayFilter filter : preFilterList) {
                filter.filter(channel);
            }
        }
    }
}
