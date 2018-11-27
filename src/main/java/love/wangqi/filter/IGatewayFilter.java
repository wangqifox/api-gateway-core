package love.wangqi.filter;

import io.netty.channel.Channel;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/9/3 下午6:18
 */
public interface IGatewayFilter {
    /**
     * 过滤Http请求
     * @param channel
     * @throws Exception
     */
    void filter(Channel channel) throws Exception;
}
