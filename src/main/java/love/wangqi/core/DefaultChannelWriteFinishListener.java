package love.wangqi.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import love.wangqi.context.ContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018-11-27 18:39
 */
public class DefaultChannelWriteFinishListener implements ChannelFutureListener {
    private Logger logger = LoggerFactory.getLogger(DefaultChannelWriteFinishListener.class);

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        Channel channel = future.channel();
        Boolean keepAlive = ContextUtil.getKeepAlive(channel);
        logger.debug("======= serverChannelId: {}", channel.id());

        ContextUtil.getRequest(channel).release();

        if (!keepAlive) {
            channel.close();
        }
    }
}
