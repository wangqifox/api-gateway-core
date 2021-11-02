package love.wangqi.route;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;

import java.util.List;
import java.util.Set;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/5/30 上午9:43
 */
public interface RouteMapper {
    /**
     * 根据路径获取Route
     *
     * @param path
     * @param method
     * @return
     */
    Route getRoute(String path, HttpMethod method);

    /**
     * 根据请求获取Route
     *
     * @param request
     * @return
     */
    Route getRoute(HttpRequest request);

    /**
     * 刷新路由
     *
     * @param ids 路由id的列表
     */
    void refresh(Set<Long> ids);

    /**
     * 获取路由列表
     *
     * @return
     */
    List<Route> getRouteList();
}
