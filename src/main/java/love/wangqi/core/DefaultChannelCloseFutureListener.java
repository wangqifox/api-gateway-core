package love.wangqi.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import love.wangqi.context.ContextUtil;
import love.wangqi.listener.ChannelCloseFutureListener;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/11/22 下午4:21
 */
public class DefaultChannelCloseFutureListener implements ChannelCloseFutureListener {

    @Override
    public void operationComplete(Channel channel, ChannelFuture future) {
        Boolean keepAlive = ContextUtil.getKeepAlive(channel);

        ContextUtil.getRequest(channel).release();

        if (!keepAlive) {
            channel.close();
        }
    }
}
