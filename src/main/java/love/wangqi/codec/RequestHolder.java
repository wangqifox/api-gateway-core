package love.wangqi.codec;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import love.wangqi.route.Route;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import static love.wangqi.context.Constants.HTTP;
import static love.wangqi.context.Constants.HTTPS;

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

    public String getHost() {
        if (url.getHost() == null) {
            throw new RuntimeException("no host found");
        }
        return url.getHost();
    }

    public int getPort() {
        String protocol = url.getProtocol() == null ? HTTP : url.getProtocol();
        int port = url.getPort();
        if (port == -1) {
            if (HTTP.equalsIgnoreCase(protocol)) {
                port = 80;
            } else if (HTTPS.equalsIgnoreCase(protocol)) {
                port = 443;
            }
        }
        return port;
    }

    public String getProtocol() {
        return url.getProtocol();
    }

    public InetSocketAddress getSocketAddress() {
        return new InetSocketAddress(getHost(), getPort());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{route, url});
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof RequestHolder) {
            return this.route.equals(((RequestHolder) obj).route) && this.url.equals(((RequestHolder) obj).url);
        }
        return false;
    }

    public static void main(String[] args) throws MalformedURLException {
        RequestHolder requestHolder1 = new RequestHolder(new Route(9L, HttpMethod.GET, "/html", new URL("http://10.100.64.71/html/user.json")), new URL("http://10.100.64.71/html/user.json"), null, null);
        RequestHolder requestHolder2 = new RequestHolder(new Route(9L, HttpMethod.GET, "/html", new URL("http://10.100.64.71/html/user.json")), new URL("http://10.100.64.71/html/user.json"), null, null);
        System.out.println(requestHolder1.hashCode());
        System.out.println(requestHolder2.hashCode());
        System.out.println(requestHolder1.equals(requestHolder2));

        InetSocketAddress socketAddress1 = new InetSocketAddress("127.0.0.1", 80);
        InetSocketAddress socketAddress2 = new InetSocketAddress("127.0.0.1", 80);
        System.out.println(socketAddress1.hashCode());
        System.out.println(socketAddress2.hashCode());
        System.out.println(socketAddress1.equals(socketAddress2));
    }
}