package love.wangqi.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/5/26 22:21
 */
public class BackendFilter extends ChannelInitializer<SocketChannel> {
    private final SslContext sslCtx;
    private final ChannelHandlerContext ctx;
    private final Integer readTimeout;

    public BackendFilter(SslContext sslCtx, ChannelHandlerContext ctx, Integer readTimeout) {
        this.sslCtx = sslCtx;
        this.ctx = ctx;
        this.readTimeout = readTimeout;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }
        pipeline.addLast(new HttpClientCodec());
        pipeline.addLast(new HttpContentDecompressor());
        pipeline.addLast(new HttpObjectAggregator(1024 * 1024 * 64));
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS));
        pipeline.addLast(new BackendHandler(ctx));
    }
}
