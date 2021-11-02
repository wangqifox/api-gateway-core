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
     *
     * @param routeMapper
     * @return
     */
    HttpRequestBuilder setRouteMapper(RouteMapper routeMapper);

    /**
     * 生成新的请求
     *
     * @param originRequest
     * @return
     * @throws Exception
     */
    RequestHolder build(FullHttpRequest originRequest) throws Exception;

    /**
     * 获取路由
     *
     * @param originRequest
     * @return
     */
    Route getRoute(FullHttpRequest originRequest);
}
