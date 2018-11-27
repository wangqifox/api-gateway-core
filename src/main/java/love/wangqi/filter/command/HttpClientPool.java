package love.wangqi.filter.command;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.ChannelPoolMap;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import love.wangqi.codec.RequestHolder;
import love.wangqi.context.Attributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018-11-27 16:14
 */
public class HttpClientPool {
    private final static Logger logger = LoggerFactory.getLogger(HttpClientPool.class);

    private final EventLoopGroup group = new NioEventLoopGroup();
    private final Bootstrap bootstrap = new Bootstrap();
    private ChannelPoolMap<RequestHolder, SimpleChannelPool> poolMap;

    public static final HttpClientPool INSTANCE = new HttpClientPool();

    private HttpClientPool() {
        bootstrap
                .group(group)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 500)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true);

        poolMap = new AbstractChannelPoolMap<RequestHolder, SimpleChannelPool>() {
            @Override
            protected SimpleChannelPool newPool(RequestHolder requestHolder) {
                logger.debug("address: {}", requestHolder.getSocketAddress());
                return new FixedChannelPool(bootstrap.remoteAddress(requestHolder.getSocketAddress()), new HttpPoolHandler(requestHolder), 1);
            }
        };
    }

    public void request(RequestHolder requestHolder, Channel serverChannel) {
        logger.debug("requestHolder.hashCode: {}", requestHolder.hashCode());
        final SimpleChannelPool pool = poolMap.get(requestHolder);
        Future<Channel> f = pool.acquire();
        f.addListener((FutureListener<Channel>) future -> {
            if (future.isSuccess()) {
                HttpRequest request = requestHolder.request;
                HttpPostRequestEncoder bodyRequestEncoder = requestHolder.bodyRequestEncoder;

                Channel clientChannel = future.getNow();
                clientChannel.attr(Attributes.SERVER_CHANNEL).set(serverChannel);
                clientChannel.write(request);
                if (bodyRequestEncoder != null && bodyRequestEncoder.isChunked()) {
                    clientChannel.write(bodyRequestEncoder);
                }
                clientChannel.flush();

                pool.release(clientChannel);
            }
        });
    }
}
