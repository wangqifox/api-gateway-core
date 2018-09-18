package love.wangqi.filter;

import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/9/3 下午6:18
 */
public interface IGatewayFilter {
    /**
     * 过滤Http请求
     * @param httpRequest
     * @throws Exception
     */
    void filter(FullHttpRequest httpRequest) throws Exception;
}
