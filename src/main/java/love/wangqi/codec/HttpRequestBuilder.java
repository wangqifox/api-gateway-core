package love.wangqi.codec;

import io.netty.handler.codec.http.FullHttpRequest;
import love.wangqi.route.Route;
import love.wangqi.route.RouteMapper;


/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/5/30 下午6:58
 */
public interface HttpRequestBuilder {
    /**
     * 设置路由映射器
     * @param routeMapper
     * @return
     */
    DefaultHttpRequestBuilder setRouteMapper(RouteMapper routeMapper);

    /**
     * 设置原始请求
     * @param originRequest
     * @return
     */
    DefaultHttpRequestBuilder setOriginRequest(FullHttpRequest originRequest);

    /**
     * 生成新的请求
     * @return
     * @throws Exception
     */
    DefaultHttpRequestBuilder.RequestHolder build() throws Exception;

    /**
     * 获取路由
     * @return
     */
    Route getRoute();
}
