package love.wangqi.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import love.wangqi.exception.GatewayNoRouteException;
import love.wangqi.route.Route;
import love.wangqi.route.RouteMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/5/29 下午7:08
 */
public class DefaultHttpRequestBuilder implements HttpRequestBuilder {
    private static final Logger logger = LoggerFactory.getLogger(DefaultHttpRequestBuilder.class);

    private RouteMapper routeMapper;
    private HttpRequest newRequest;
    private HttpPostRequestEncoder newBodyRequestEncoder;

    protected HttpRequestDecomposer httpRequestDecomposer;

    HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);

    public DefaultHttpRequestBuilder() {
    }

    @Override
    public HttpRequestBuilder setRouteMapper(RouteMapper routeMapper) {
        this.routeMapper = routeMapper;
        return this;
    }

    @Override
    public Route getRoute(FullHttpRequest originRequest) {
        return routeMapper.getRoute(originRequest);
    }

    @Override
    public RequestHolder build(FullHttpRequest originRequest) throws Exception {
        httpRequestDecomposer = new HttpRequestDecomposer(originRequest);
        Route route = getRoute(originRequest);
        if (route == null) {
            throw new GatewayNoRouteException();
        }
        URL url = route.getMapUrl();
        logger.info("proxy_pass {}", url.toString());

        // 请求路径
        QueryStringEncoder queryStringEncoder = new QueryStringEncoder(url.getPath());
        // 请求参数
        buildParams(route).forEach((key, values) -> values.forEach(value -> {
            queryStringEncoder.addParam(key, value);
        }));
        newRequest = new DefaultFullHttpRequest(originRequest.protocolVersion(), originRequest.method(), new URI(queryStringEncoder.toString()).toASCIIString());
        // 请求头
        buildHeaders(route).forEach((key, values) -> values.forEach(value -> {
            newRequest.headers().set(key, value);
        }));
        newRequest.headers().remove(HttpHeaderNames.COOKIE);
        newRequest.headers().set(HttpHeaderNames.HOST, url.getHost());
        HttpUtil.setKeepAlive(newRequest, true);

        // 请求体
        String contentType = httpRequestDecomposer.getContentType();
        if (contentType != null) {
            if (contentType.startsWith(HttpHeaderValues.APPLICATION_JSON.toString())) {
                ByteBuf bbuf = Unpooled.copiedBuffer(buildContentJson(route), StandardCharsets.UTF_8);
                newRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, bbuf.readableBytes());
                ((FullHttpRequest) newRequest).content().writeBytes(bbuf);
            } else if (contentType.startsWith(HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())) {
                HttpPostRequestEncoder requestEncoder = new HttpPostRequestEncoder(newRequest, false);
                buildContentFormUrlEncoded(route).forEach((key, values) -> {
                    values.forEach(value -> {
                        try {
                            requestEncoder.addBodyAttribute(key, value);
                        } catch (HttpPostRequestEncoder.ErrorDataEncoderException e) {
                            logger.error(e.getMessage());
                        }
                    });
                });
                newRequest = requestEncoder.finalizeRequest();
            } else if (contentType.startsWith(HttpHeaderValues.MULTIPART_FORM_DATA.toString())) {
                HttpPostRequestEncoder requestEncoder = new HttpPostRequestEncoder(factory, newRequest, true);
                for (InterfaceHttpData data : buildContentFormData(route)) {
                    if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                        requestEncoder.addBodyHttpData(data);
                    } else if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
                        requestEncoder.addBodyHttpData(data);
                    }
                }
                newRequest = requestEncoder.finalizeRequest();
                newBodyRequestEncoder = requestEncoder;
            } else {
                ByteBuf byteBuf = buildContentOther(route);
                newRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
                ((FullHttpRequest) newRequest).content().writeBytes(byteBuf);
            }
        }
        return new RequestHolder(route, url, newRequest, newBodyRequestEncoder);
    }

    /**
     * 返回path（不包含?后面的参数部分）
     *
     * @return
     */
    protected String buildPath(Route route) {
        return httpRequestDecomposer.getPath();
    }

    /**
     * 返回请求的请求参数
     *
     * @return
     */
    protected Map<String, List<String>> buildParams(Route route) {
        return httpRequestDecomposer.getParams();
    }

    /**
     * 返回请求的请求头
     *
     * @return
     */
    protected Map<String, List<String>> buildHeaders(Route route) {
        return httpRequestDecomposer.getHeaders();
    }

    /**
     * 如果content-type为application/json，获取请求体
     *
     * @return
     */
    protected String buildContentJson(Route route) {
        return httpRequestDecomposer.getContentJsonAsString();
    }

    /**
     * 如果content-type为application/x-www-form-urlencoded，获取请求体
     *
     * @return
     */
    protected Map<String, List<String>> buildContentFormUrlEncoded(Route route) {
        return httpRequestDecomposer.getContentFormUrlEncoded();
    }

    /**
     * 如果content-type为multipart/form-data，获取请求体
     *
     * @return
     */
    protected List<InterfaceHttpData> buildContentFormData(Route route) {
        return httpRequestDecomposer.getContentFormdata();
    }

    /**
     * 其他类型的content-type则直接返回相应的ByteBuf
     *
     * @return
     */
    private ByteBuf buildContentOther(Route route) {
        return httpRequestDecomposer.getContentOther();
    }

}
