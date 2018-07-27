package love.wangqi.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import love.wangqi.config.GatewayConfig;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/6/4 下午7:47
 */
public interface HttpRequestFilter {
    /**
     * 过滤Http请求
     * @param config
     * @param ctx
     * @param httpRequest
     * @throws Exception
     */
    void filter(GatewayConfig config, ChannelHandlerContext ctx, FullHttpRequest httpRequest) throws Exception;
}
