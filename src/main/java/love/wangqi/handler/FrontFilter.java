package love.wangqi.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import love.wangqi.config.GatewayConfig;


/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/5/26 21:57
 */
public class FrontFilter extends ChannelInitializer<SocketChannel> {
    private Logger logger = LoggerFactory.getLogger(FrontFilter.class);

    private GatewayConfig config = GatewayConfig.getInstance();

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline ph = ch.pipeline();

        config.getChannelInboundHandlerList().forEach(ph::addLast);
        config.getChannelOutboundHandlerList().forEach(ph::addLast);
        ph.addLast(new HttpResponseEncoder());
        config.getHttpResponseHandlerList().forEach(ph::addLast);
        ph.addLast(new HttpRequestDecoder());
        ph.addLast(new ChunkedWriteHandler());
        ph.addLast(new HttpObjectAggregator(10 * 1024 * 1024));
        ph.addLast(new FrontHandler());

        ch.closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                logger.debug("channel close");
            }
        });
    }
}
