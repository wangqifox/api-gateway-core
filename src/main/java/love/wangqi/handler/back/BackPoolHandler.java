package love.wangqi.handler.back;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.stream.ChunkedWriteHandler;
import love.wangqi.codec.RequestHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;

import static love.wangqi.context.Constants.HTTPS;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018-11-27 16:24
 */
public class BackPoolHandler implements ChannelPoolHandler {
    private static final Logger logger = LoggerFactory.getLogger(BackPoolHandler.class);

    private SslContext sslCtx = null;

    public BackPoolHandler(RequestHolder requestHolder) {
        if (requestHolder.getProtocol().equalsIgnoreCase(HTTPS)) {
            try {
                sslCtx = SslContextBuilder.forClient()
                        .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
            } catch (SSLException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void channelReleased(Channel ch) throws Exception {
        logger.debug("channelReleased. Channel ID: {}", ch.id());
    }

    @Override
    public void channelAcquired(Channel ch) throws Exception {
        logger.debug("channelAcquired. Channel ID: {}", ch.id());
    }

    @Override
    public void channelCreated(Channel ch) throws Exception {
        logger.debug("channelCreated. Channel ID: {}", ch.id());
        NioSocketChannel channel = (NioSocketChannel) ch;
        channel.config().setKeepAlive(true);
        channel.config().setTcpNoDelay(true);
        ChannelPipeline pipeline = channel.pipeline();
        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }
        pipeline.addLast(new HttpClientCodec());
        pipeline.addLast(new HttpContentDecompressor());
        pipeline.addLast(new HttpObjectAggregator(1024 * 1024 * 64));
        pipeline.addLast(new ChunkedWriteHandler());
//        pipeline.addLast(new ReadTimeoutHandler(requestHolder.route.getTimeoutInMilliseconds(), TimeUnit.MILLISECONDS));
        pipeline.addLast(new BackHandler());
    }
}
