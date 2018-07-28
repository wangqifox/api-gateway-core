package love.wangqi.codec;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import love.wangqi.route.Route;

import java.net.URL;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/7/28 22:37
 */
public class RequestHolder {
    public Route route;
    public URL url;
    public HttpRequest request;
    public HttpPostRequestEncoder bodyRequestEncoder;

    public RequestHolder(Route route, URL url, HttpRequest request, HttpPostRequestEncoder bodyRequestEncoder) {
        this.route = route;
        this.url = url;
        this.request = request;
        this.bodyRequestEncoder = bodyRequestEncoder;
    }
}