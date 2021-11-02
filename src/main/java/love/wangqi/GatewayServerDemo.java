package love.wangqi;


import io.netty.handler.codec.http.HttpMethod;
import love.wangqi.codec.DefaultHttpRequestBuilder;
import love.wangqi.config.GatewayConfig;
import love.wangqi.core.DefaultChannelWriteFinishListener;
import love.wangqi.core.ResponseHandler;
import love.wangqi.exception.handler.DefaultExceptionHandler;
import love.wangqi.route.AbstractRouteMapper;
import love.wangqi.route.Route;
import love.wangqi.route.RouteMapper;
import love.wangqi.server.GatewayServer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/5/30 下午6:22
 */
public class GatewayServerDemo {

    public static void main(String[] args) {
        RouteMapper routeMapper = new AbstractRouteMapper() {
            @Override
            protected List<Route> locateRouteList(Set<Long> ids) {
                List<Route> routeList = new ArrayList<>();
                try {
                    routeList.add(new Route(1L, HttpMethod.GET, "/", new URL("https://blog.wangqi.love/")));
                    routeList.add(new Route(1L, HttpMethod.GET, "/wangqi", new URL("http://wangqi.love/")));
                    routeList.add(new Route(1L, HttpMethod.GET, "/117", new URL("http://10.0.111.117/#/login")));
                    routeList.add(new Route(2L, HttpMethod.GET, "/baidu", new URL("https://www.baidu.com/")));
                    routeList.add(new Route(3L, HttpMethod.GET, "/taobao", new URL("http://www.taobao.com/")));
                    routeList.add(new Route(4L, HttpMethod.GET, "/github", new URL("https://github.com/")));
                    routeList.add(new Route(5L, HttpMethod.GET, "/oschina", new URL("https://www.oschina.net/")));
                    routeList.add(new Route(6L, HttpMethod.POST, "/users/{id}", new URL("http://127.0.0.1/pre/users/{id}")));
                    routeList.add(new Route(7L, HttpMethod.GET, "/css/main.css", new URL("https://blog.wangqi.love/css/main.css")));
                    routeList.add(new Route(8L, HttpMethod.GET, "/path", new URL("http://127.0.0.1:9990/path")));
                    routeList.add(new Route(9L, HttpMethod.GET, "/html", new URL("http://10.100.64.71/html/user.json")));
                    routeList.add(new Route(10L, HttpMethod.GET, "/css", new URL("https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/mantpl/css/news/init_7637f86c.css")));
                    routeList.add(new Route(11L, HttpMethod.GET, "/google", new URL("https://www.google.com")));
                    routeList.add(new Route(12L, HttpMethod.GET, "/local", new URL("http://127.0.0.1:9999")));
                } catch (MalformedURLException e) {
                }
                return routeList;
            }
        };
        routeMapper.refresh(null);
        GatewayConfig config = GatewayConfig.getInstance();
        config.setPort(8888);
        config.setHttpRequestBuilder(new DefaultHttpRequestBuilder());
        config.setRouteMapper(routeMapper);
        config.setChannelWriteFinishListener(new DefaultChannelWriteFinishListener());
        config.setResponseHandler(new ResponseHandler());
        config.setExceptionHandler(new DefaultExceptionHandler());

        GatewayServer server = new GatewayServer();
        server.start();
    }

}