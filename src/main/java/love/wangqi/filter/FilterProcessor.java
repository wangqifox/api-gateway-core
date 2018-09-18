package love.wangqi.filter;

import com.netflix.hystrix.exception.HystrixRuntimeException;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import love.wangqi.config.GatewayConfig;
import love.wangqi.exception.GatewayException;

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

    private FilterProcessor() {}

    public void preRoute(FullHttpRequest httpRequest) throws GatewayException {
        try {
            runFilters("pre", httpRequest);
        } catch (GatewayException e) {
            throw e;
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            throw new GatewayException(HttpResponseStatus.INTERNAL_SERVER_ERROR, "UNCAUGHT_EXCEPTION_IN_PRE_FILTER_" + e.getClass().getName());
        }
    }

    public void route(FullHttpRequest httpRequest) throws GatewayException {
        try {
            runFilters("route", httpRequest);
        } catch (GatewayException e) {
            throw e;
        } catch (HystrixRuntimeException hre) {
            throw new GatewayException(HttpResponseStatus.INTERNAL_SERVER_ERROR, hre.getMessage());
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            throw new GatewayException(HttpResponseStatus.INTERNAL_SERVER_ERROR, "UNCAUGHT_EXCEPTION_IN_PRE_FILTER_" + e.getClass().getName());
        }
    }

    public void postRoute(FullHttpRequest httpRequest) throws GatewayException {
        try {
            runFilters("post", httpRequest);
        } catch (GatewayException e) {
            throw e;
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            throw new GatewayException(HttpResponseStatus.INTERNAL_SERVER_ERROR, "UNCAUGHT_EXCEPTION_IN_PRE_FILTER_" + e.getClass().getName());
        }
    }

    public void error(FullHttpRequest httpRequest) {
        try {
            runFilters("error", httpRequest);
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
    }


    public void runFilters(String type, FullHttpRequest httpRequest) throws Throwable {
        List<GatewayFilter> preFilterList = GatewayConfig.getInstance().getFiltersByType(type);
        if (preFilterList != null) {
            for (GatewayFilter filter : preFilterList) {
                filter.filter(httpRequest);
            }
        }
    }
}
