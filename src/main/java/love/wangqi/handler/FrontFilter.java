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
import love.wangqi.config.GatewayConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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
        ChannelPipeline pipeline = ch.pipeline();

        config.getChannelInboundHandlerList().forEach(pipeline::addLast);
        config.getChannelOutboundHandlerList().forEach(pipeline::addLast);
        pipeline.addLast(new HttpResponseEncoder());
        config.getHttpResponseHandlerList().forEach(pipeline::addLast);
        pipeline.addLast(new HttpRequestDecoder());
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new HttpObjectAggregator(10 * 1024 * 1024));
        pipeline.addLast(new FrontHandler());
        pipeline.addLast(new ExceptionHandler());


        ch.closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                logger.debug("channel close");
            }
        });
    }
}
