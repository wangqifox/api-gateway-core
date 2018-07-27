package love.wangqi.listener;

import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.SocketChannel;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/6/4 下午7:37
 */
public interface ChannelCloseFutureListener {
    /**
     * Channel关闭时调用
     * @param channel
     * @param future
     */
    void operationComplete(SocketChannel channel, ChannelFuture future);
}
