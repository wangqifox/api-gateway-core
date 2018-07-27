package love.wangqi.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/5/26 22:21
 */
public class BackendFilter extends ChannelInitializer<SocketChannel> {
    private final SslContext sslCtx;
    private final Channel inboundChannel;

    public BackendFilter(SslContext sslCtx, Channel inboundChannel) {
        this.sslCtx = sslCtx;
        this.inboundChannel = inboundChannel;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        if (sslCtx != null) {
            pipeline.addLast("ssl", sslCtx.newHandler(ch.alloc()));
        }

        pipeline.addLast("codec", new HttpClientCodec());

        pipeline.addLast("inflater", new HttpContentDecompressor());

        pipeline.addLast(new HttpObjectAggregator(1048576));

        pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());

        pipeline.addLast("handler", new BackendHandler(inboundChannel));
    }
}
