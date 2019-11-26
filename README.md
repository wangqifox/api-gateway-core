# api-gateway-core

[![Build Status](https://travis-ci.org/wangqifox/api-gateway-core.svg?branch=master)](https://travis-ci.org/wangqifox/api-gateway-core)
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

`api-gateway-core`是一个轻量级的api网关

## Background

随着公司各个项目的扩展，不同的项目之间和第三方出现了大量调用项目API的需求。此时就面临了一系列问题，例如：如何让各个项目安全地对外开放接口、如何让调用方快速接入、如何保证接口的安全等等。最初的时候，这些工作是各个项目自己做的，这段时期的接口对接是一个极其痛苦的过程：各个项目的权限控制不一样、文档不全，接口提供方和调用方都需要经过大量重复的沟通。也是我们需要一个隔离接口提供方和调用方的中间层——API网关，它负责在抽象出各个业务需要的通用功能，例如：权限验证、限流、超时控制、熔断降级。

## Usage

```java
// 网关的映射关系
RouteMapper routeMapper = new AbstractRouteMapper() {
    @Override
    protected List<Route> locateRouteList(Set<Long> ids) {
        List<Route> routeList = new ArrayList<>();
        try {
            routeList.add(new Route(1L, HttpMethod.GET, "/", new URL("https://blog.wangqi.love/")));
            routeList.add(new Route(2L, HttpMethod.GET, "/baidu", new URL("https://www.baidu.com/")));
            routeList.add(new Route(3L, HttpMethod.GET, "/taobao", new URL("https://www.taobao.com/")));
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
```

## Related

[API网关技术总结](https://blog.wangqi.love/articles/Java/API网关技术总结.html)

## License

[Apache License 2.0](LICENSE)