package love.wangqi.route;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import love.wangqi.util.AntPathMatcher;
import love.wangqi.util.PathMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/5/30 上午9:48
 */
public abstract class AbstractRouteMapper implements RouteMapper {
    private final Logger logger = LoggerFactory.getLogger(AbstractRouteMapper.class);
    private PathMatcher pathMatcher = new AntPathMatcher();
    private List<Route> routeList;

    /**
     * 遍历所有的路由，返回符合请求的路由
     *
     * @param path
     * @param method
     * @return
     */
    @Override
    public Route getRoute(String path, HttpMethod method) {
        for (Route route : getRouteList()) {
            if (!method.equals(route.getMethod())) {
                continue;
            }
            if (route.getPath().equals(path)) {
                route = route.clone();
                return route;
            }
            if (this.pathMatcher.match(route.getPath(), path)) {
                route = route.clone();
                Map<String, String> uriTemplateVariables = this.pathMatcher.extractUriTemplateVariables(route.getPath(), path);
                if (!uriTemplateVariables.isEmpty()) {
                    String mapUrl = route.getMapUrl().toString();
                    for (Map.Entry<String, String> entry : uriTemplateVariables.entrySet()) {
                        mapUrl = mapUrl.replaceAll(String.format("\\{%s}", entry.getKey()), entry.getValue());
                    }
                    try {
                        route.setMapUrl(new URL(mapUrl));
                    } catch (MalformedURLException e) {
                        logger.error(e.getMessage());
                    }
                }

                return route;
            }
        }
        return null;
    }

    @Override
    public Route getRoute(HttpRequest request) {
        try {
            return getRoute(new URI(request.uri()).getPath(), request.method());
        } catch (URISyntaxException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    /**
     * 获取路由列表
     *
     * @param ids 路由id的列表
     * @return
     */
    protected abstract List<Route> locateRouteList(Set<Long> ids);

    @Override
    public List<Route> getRouteList() {
        return routeList;
    }

    @Override
    public void refresh(Set<Long> ids) {
        routeList = locateRouteList(ids);
    }
}
