package love.wangqi.route;

import io.netty.handler.codec.http.HttpMethod;

import java.net.URL;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/5/30 上午9:43
 */
public class Route {
    /**
     * id
     */
    private Long id;
    /**
     * 前端路径
     */
    private String path;
    /**
     * 后端映射的URL
     */
    private URL mapUrl;
    /**
     * 请求方法
     */
    private HttpMethod method;
    /**
     * 请求超时时间
     */
    private Integer timeoutInMilliseconds;

    private static final Integer DEFAULT_TIMEOUT = 3000;

    public Route() {
    }

    public Route(Long id, HttpMethod method, String path, URL mapUrl) {
        this(id, method, path, mapUrl, DEFAULT_TIMEOUT);
    }

    public Route(Long id, HttpMethod method, String path, URL mapUrl, Integer timeoutInMilliseconds) {
        this.id = id;
        this.path = path;
        this.mapUrl = mapUrl;
        this.method = method;
        this.timeoutInMilliseconds = timeoutInMilliseconds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public URL getMapUrl() {
        return mapUrl;
    }

    public void setMapUrl(URL mapUrl) {
        this.mapUrl = mapUrl;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public Integer getTimeoutInMilliseconds() {
        return timeoutInMilliseconds;
    }

    public void setTimeoutInMilliseconds(Integer timeoutInMilliseconds) {
        this.timeoutInMilliseconds = timeoutInMilliseconds;
    }
}
